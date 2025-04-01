package com.linh.ecommerce.role;


import lombok.Getter;

@Getter
public enum RoleScope {
    SYSTEM("SYSTEM"),
    STORE("STORE");

    private final String description;

    RoleScope(String description) {
      this.description = description;
  }
}
