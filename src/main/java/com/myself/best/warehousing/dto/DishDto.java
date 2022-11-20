package com.myself.best.warehousing.dto;

import com.myself.best.warehousing.entity.Dish;
import com.myself.best.warehousing.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
