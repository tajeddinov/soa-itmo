package com.itmo.soa.contract.request.movie;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesDto implements Serializable {

    @NotNull(message = "Поле обязательно")
    private Integer x;

    @NotNull(message = "Поле обязательно")
    private Long y;
}
