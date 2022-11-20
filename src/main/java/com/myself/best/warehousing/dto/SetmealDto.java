package com.myself.best.warehousing.dto;


import com.myself.best.warehousing.entity.Setmeal;
import com.myself.best.warehousing.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
