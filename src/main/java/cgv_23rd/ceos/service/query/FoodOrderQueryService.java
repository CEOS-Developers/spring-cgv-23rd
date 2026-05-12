package cgv_23rd.ceos.service.query;

import cgv_23rd.ceos.dto.food.response.FoodOrderResponseDto;
import cgv_23rd.ceos.entity.food.FoodOrder;
import cgv_23rd.ceos.mapper.FoodOrderMapper;
import cgv_23rd.ceos.repository.food.FoodOrderRepository;
import cgv_23rd.ceos.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodOrderQueryService {

    private final FoodOrderRepository foodOrderRepository;
    private final UserService userService;
    private final FoodOrderMapper foodOrderMapper;

    public List<FoodOrderResponseDto> getFoodOrderList(Long userId, int page, int size) {
        userService.getUser(userId);

        List<Long> orderIds = foodOrderRepository.findPageIdsByUserId(userId, PageRequest.of(page, size))
                .getContent();

        if (orderIds.isEmpty()) {
            return List.of();
        }

        List<FoodOrder> orders = foodOrderRepository.findAllByIdInWithDetails(orderIds).stream()
                .sorted(Comparator.comparing(FoodOrder::getCreatedAt).reversed())
                .toList();

        return orders.stream()
                .map(foodOrderMapper::toResponse)
                .toList();
    }
}
