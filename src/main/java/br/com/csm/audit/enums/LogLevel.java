package br.com.csm.audit.enums;

public enum LogLevel {
    ACCESS,  // Apenas requisições (GETs de listas, acessos a menus)
    AUDIT,   // Mutações de negócio (POST, PUT, DELETE, Logins)
    ERROR    // Exceções 500, bugs e stack traces
}