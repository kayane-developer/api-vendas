package io.github.kayanedeveloper.rest;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ApiErrors {

    @Getter
    private List<String> errors;

    public ApiErrors(String mensagemErro){
        this.errors = Collections.singletonList(mensagemErro);
    }

    public ApiErrors(List<String> errors) {
        this.errors = errors;
    }

}
