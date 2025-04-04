package com.linh.ecommerce.size;

import lombok.Getter;

@Getter
public enum SizeType {
    SIZE_EU("SIZE (EU)"),
    SIZE_EGE("SIZE (Tuổi)"),
    SIZE_CM("SIZE (cm)"),
    SIZE_International("SIZE (Quốc tế)"),
    SIZE_INCH("SIZE (Inch)"),
    ;

    private final String description;

    SizeType(String description) {this.description = description;}
}
