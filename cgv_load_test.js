import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate } from 'k6/metrics';

const BASE_URL = (__ENV.BASE_URL || 'http://localhost:8080').replace(/\/$/, '');
const SCENARIO = __ENV.SCENARIO || 'screenings';
const TEST_PASSWORD = __ENV.TEST_PASSWORD || 'Passw0rd!';
const SLEEP_SECONDS = Number(__ENV.SLEEP_SECONDS || 1);

const RAMP_UP_1 = __ENV.RAMP_UP_1 || '30s';
const RAMP_UP_2 = __ENV.RAMP_UP_2 || '30s';
const RAMP_DOWN = __ENV.RAMP_DOWN || '10s';
const TARGET_VU_1 = Number(__ENV.TARGET_VU_1 || 10);
const TARGET_VU_2 = Number(__ENV.TARGET_VU_2 || 30);

const reservationSuccessRate = new Rate('reservation_success_rate');
const reservationConflictRate = new Rate('reservation_conflict_rate');
const reservationCancelFailures = new Counter('reservation_cancel_failures');

export const options = {
    stages: [
        { duration: RAMP_UP_1, target: TARGET_VU_1 },
        { duration: RAMP_UP_2, target: TARGET_VU_2 },
        { duration: RAMP_DOWN, target: 0 },
    ],
    thresholds: {
        checks: ['rate>0.95'],
        http_req_duration: ['p(95)<5000'],
    },
};

function safeJson(response) {
    try {
        return response.json();
    } catch (error) {
        return null;
    }
}

function buildUrl(path) {
    return `${BASE_URL}${path}`;
}

function buildJsonHeaders(accessToken) {
    const headers = {
        'Content-Type': 'application/json',
    };

    if (accessToken) {
        headers.Authorization = `Bearer ${accessToken}`;
    }

    return headers;
}

function fail(message, response) {
    const body = response ? response.body : 'no response';
    throw new Error(`${message}: status=${response ? response.status : 'n/a'}, body=${body}`);
}

function getScreenings() {
    const response = http.get(buildUrl('/api/screenings'));

    if (response.status !== 200) {
        fail('상영 목록 조회 실패', response);
    }

    const screenings = safeJson(response);

    if (!screenings || !screenings.length) {
        throw new Error('상영 데이터가 없습니다. 테스트 전에 screening 데이터를 먼저 준비해주세요.');
    }

    return screenings;
}

function getSeatAvailability(screeningId) {
    const response = http.get(buildUrl(`/api/screenings/${screeningId}/seats`));

    if (response.status !== 200) {
        fail('좌석 조회 실패', response);
    }

    return safeJson(response);
}

function getStoreMenus(cinemaId) {
    const response = http.get(buildUrl(`/api/store/menus?cinemaId=${cinemaId}`));

    if (response.status !== 200) {
        fail('매점 메뉴 조회 실패', response);
    }

    return safeJson(response);
}

function createCredentials() {
    const runId = `${Date.now()}_${Math.floor(Math.random() * 100000)}`;

    return {
        name: `k6-user-${runId}`,
        email: `k6-user-${runId}@example.com`,
        password: TEST_PASSWORD,
    };
}

function signup(credentials) {
    const response = http.post(
        buildUrl('/api/auth/signup'),
        JSON.stringify(credentials),
        { headers: buildJsonHeaders() }
    );

    if (response.status !== 201) {
        fail('회원가입 실패', response);
    }
}

function login(credentials) {
    const response = http.post(
        buildUrl('/api/auth/login'),
        JSON.stringify({
            email: credentials.email,
            password: credentials.password,
        }),
        { headers: buildJsonHeaders() }
    );

    if (response.status !== 200) {
        fail('로그인 실패', response);
    }

    const body = safeJson(response);

    if (!body || !body.accessToken) {
        fail('로그인 응답에서 accessToken을 찾지 못했습니다', response);
    }

    return body.accessToken;
}

function findTargetScreening(screenings) {
    const requestedScreeningId = __ENV.SCREENING_ID ? Number(__ENV.SCREENING_ID) : null;

    if (requestedScreeningId) {
        for (let i = 0; i < screenings.length; i += 1) {
            if (screenings[i].screeningId === requestedScreeningId) {
                return screenings[i];
            }
        }

        throw new Error(`SCREENING_ID=${requestedScreeningId} 에 해당하는 상영이 없습니다.`);
    }

    return screenings[0];
}

function findTargetMenu(menus) {
    const requestedStoreMenuId = __ENV.STORE_MENU_ID ? Number(__ENV.STORE_MENU_ID) : null;

    if (requestedStoreMenuId) {
        for (let i = 0; i < menus.length; i += 1) {
            if (menus[i].storeMenuId === requestedStoreMenuId) {
                return menus[i];
            }
        }

        throw new Error(`STORE_MENU_ID=${requestedStoreMenuId} 에 해당하는 메뉴가 없습니다.`);
    }

    for (let i = 0; i < menus.length; i += 1) {
        if (menus[i].stockQuantity > 0) {
            return menus[i];
        }
    }

    throw new Error('재고가 남아 있는 매점 메뉴가 없습니다.');
}

function pickAvailableSeat(seatResponse) {
    if (!seatResponse || !seatResponse.seats || !seatResponse.seats.length) {
        return null;
    }

    const availableSeats = seatResponse.seats.filter((seat) => !seat.reserved);

    if (!availableSeats.length) {
        return null;
    }

    const index = (__VU + __ITER) % availableSeats.length;
    return availableSeats[index];
}

function runScreeningsScenario() {
    const response = http.get(buildUrl('/api/screenings'));

    check(response, {
        'screenings status is 200': (r) => r.status === 200,
    });
}

function runSeatsScenario(data) {
    const response = http.get(buildUrl(`/api/screenings/${data.screeningId}/seats`));

    check(response, {
        'seats status is 200': (r) => r.status === 200,
        'seats response has seats': (r) => {
            const body = safeJson(r);
            return body && body.seats && body.seats.length > 0;
        },
    });
}

function runReservationScenario(data) {
    const seatResponse = http.get(buildUrl(`/api/screenings/${data.screeningId}/seats`));

    const seatCheckPassed = check(seatResponse, {
        'reservation seat lookup is 200': (r) => r.status === 200,
    });

    if (!seatCheckPassed) {
        reservationSuccessRate.add(false);
        reservationConflictRate.add(false);
        return;
    }

    const seatBody = safeJson(seatResponse);
    const targetSeat = pickAvailableSeat(seatBody);

    if (!targetSeat) {
        reservationSuccessRate.add(false);
        reservationConflictRate.add(false);
        sleep(SLEEP_SECONDS);
        return;
    }

    const createResponse = http.post(
        buildUrl('/api/reservations'),
        JSON.stringify({
            screeningId: data.screeningId,
            seatTemplateIds: [targetSeat.seatTemplateId],
        }),
        { headers: buildJsonHeaders(data.accessToken) }
    );

    const createPassed = check(createResponse, {
        'reservation create status is 201 or 409': (r) => r.status === 201 || r.status === 409,
    });

    if (!createPassed) {
        reservationSuccessRate.add(false);
        reservationConflictRate.add(false);
        sleep(SLEEP_SECONDS);
        return;
    }

    if (createResponse.status === 409) {
        reservationSuccessRate.add(false);
        reservationConflictRate.add(true);
        sleep(SLEEP_SECONDS);
        return;
    }

    reservationSuccessRate.add(true);
    reservationConflictRate.add(false);

    const reservationBody = safeJson(createResponse);
    const reservationId = reservationBody ? reservationBody.reservationId : null;

    if (!reservationId) {
        reservationCancelFailures.add(1);
        sleep(SLEEP_SECONDS);
        return;
    }

    const cancelResponse = http.del(
        buildUrl(`/api/reservations/${reservationId}`),
        null,
        { headers: buildJsonHeaders(data.accessToken) }
    );

    const cancelPassed = check(cancelResponse, {
        'reservation cancel status is 204': (r) => r.status === 204,
    });

    if (!cancelPassed) {
        reservationCancelFailures.add(1);
    }
}

function runStoreScenario(data) {
    const response = http.post(
        buildUrl('/api/store/purchases'),
        JSON.stringify({
            cinemaId: data.cinemaId,
            storeMenuId: data.storeMenuId,
            quantity: 1,
        }),
        { headers: buildJsonHeaders(data.accessToken) }
    );

    check(response, {
        'store purchase status is 201': (r) => r.status === 201,
    });
}

export function setup() {
    const screenings = getScreenings();
    const targetScreening = findTargetScreening(screenings);

    const setupData = {
        scenario: SCENARIO,
        screeningId: targetScreening.screeningId,
        cinemaId: targetScreening.cinemaId,
    };

    if (SCENARIO === 'reservation' || SCENARIO === 'store') {
        const credentials = createCredentials();
        signup(credentials);
        setupData.accessToken = login(credentials);
        setupData.email = credentials.email;
    }

    if (SCENARIO === 'reservation') {
        const seatResponse = getSeatAvailability(setupData.screeningId);

        if (!pickAvailableSeat(seatResponse)) {
            throw new Error('선택한 상영에 예약 가능한 좌석이 없습니다.');
        }
    }

    if (SCENARIO === 'store') {
        const menus = getStoreMenus(setupData.cinemaId);
        const targetMenu = findTargetMenu(menus);
        setupData.storeMenuId = targetMenu.storeMenuId;
    }

    return setupData;
}

export default function (data) {
    if (SCENARIO === 'screenings') {
        runScreeningsScenario();
    } else if (SCENARIO === 'seats') {
        runSeatsScenario(data);
    } else if (SCENARIO === 'reservation') {
        runReservationScenario(data);
    } else if (SCENARIO === 'store') {
        runStoreScenario(data);
    } else {
        throw new Error(`지원하지 않는 SCENARIO 입니다: ${SCENARIO}`);
    }

    sleep(SLEEP_SECONDS);
}

export function handleSummary(data) {
    const sanitized = JSON.parse(JSON.stringify(data));

    if (sanitized.setup_data && sanitized.setup_data.accessToken) {
        sanitized.setup_data.accessToken = '[redacted]';
    }

    const summary = {
        scenario: SCENARIO,
        vusMax: data.metrics.vus_max ? data.metrics.vus_max.values.max : null,
        iterations: data.metrics.iterations ? data.metrics.iterations.values.count : null,
        httpReqDurationP95: data.metrics.http_req_duration
            ? data.metrics.http_req_duration.values['p(95)']
            : null,
        httpReqFailedRate: data.metrics.http_req_failed
            ? data.metrics.http_req_failed.values.rate
            : null,
        checksRate: data.metrics.checks ? data.metrics.checks.values.rate : null,
    };

    const result = {
        stdout: `${JSON.stringify(summary, null, 2)}\n`,
    };

    result[`k6-${SCENARIO}-summary.json`] = JSON.stringify(sanitized, null, 2);

    return result;
}
