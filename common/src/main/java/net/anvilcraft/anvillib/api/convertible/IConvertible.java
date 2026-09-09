package net.anvilcraft.anvillib.api.convertible;

import java.util.Optional;

public interface IConvertible {

    <T> Optional<T> convertTo(Class<T> type);
    
}