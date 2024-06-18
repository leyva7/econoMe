package com.econoMe.gestorgastosback.aspect;

import com.econoMe.gestorgastosback.exception.*;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataAccessException;

@Aspect
@Component
public class ExceptionHandlingAspect {

    // Define el punto de corte para todos los métodos en paquetes específicos
    @Pointcut("execution(* com.econoMe.gestorgastosback.service..*(..))")
    public void serviceLayer() {
    }

    // Define el consejo alrededor del punto de corte
    @Around("serviceLayer()")
    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // Intenta ejecutar el método objetivo
            return joinPoint.proceed();
        } catch (DataAccessException e) {
            // Captura excepciones de acceso a datos y lanza excepciones personalizadas según el servicio
            String className = joinPoint.getTarget().getClass().getSimpleName();
            switch(className) {
                case "AccountingService":
                    throw new AccountingException("Error de contabilidad", e);
                case "UserService":
                    throw new UserException("Error de usuario:", e);
                case "OperationService":
                    throw new OperationException("Error de operación:", e);
                case "RoleService":
                    throw new RoleException("Error de rol:", e);
                case "RegisterService":
                    throw new UserException("Error de registro:", e);
                case "MappingService":
                    throw new MappingException("Error de mapeo: ", e);
                default:
                    // Si el servicio no coincide, vuelve a lanzar la excepción original
                    throw e;
            }
        }
    }
}
