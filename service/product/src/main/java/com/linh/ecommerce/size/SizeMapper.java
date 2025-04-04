package com.linh.ecommerce.size;

import org.springframework.stereotype.Service;

@Service
public class SizeMapper {

    public Size toSize(SizeRequest request) {
        return Size.builder()
                .type(request.type())
                .value(request.value())
                .build();
    }

    public SizeResponse toSizeResponse(Size size) {
        return new SizeResponse(
                size.getId(),
                size.getType(),
                size.getValue()
        );
    }
}
