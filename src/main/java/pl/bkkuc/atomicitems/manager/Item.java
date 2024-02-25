package pl.bkkuc.atomicitems.manager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Item {

    String name;

    public Item(String name){
        this.name = name;
    }
}
