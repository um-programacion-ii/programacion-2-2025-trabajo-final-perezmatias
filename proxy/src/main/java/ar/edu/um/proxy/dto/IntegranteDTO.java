package ar.edu.um.proxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntegranteDTO {
    private String nombre;
    private String apellido;
    private String identificacion;
}