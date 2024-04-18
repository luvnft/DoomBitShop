package com.doombitshop.utils;

import com.doombitshop.dto.OrderDTO;
import com.doombitshop.dto.ProductDTO;
import com.doombitshop.dto.UserDTO;
import com.doombitshop.model.Order;
import com.doombitshop.model.Product;
import com.doombitshop.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")

public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(source = "id", target = "id")
    UserDTO userToUserDTO(User user);

    @Mapping(source = "id", target = "id")
    User userDtoToUser(UserDTO userDTO);

    @Mapping(source = "orderId", target = "orderId")
    Order orderDtoToOrder(OrderDTO orderDto);

    @Mapping(source = "orderId", target = "orderId")
    OrderDTO orderToOrderDto(Order order);
    @Mapping(source ="name", target="name" )
    Product productDtoToProduct(ProductDTO productDto);
}
