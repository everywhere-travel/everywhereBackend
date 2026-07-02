package com.everywhere.backend.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class OperacionLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperacionLogAspect.class);
    private static final String FILE_NAME = "registro_operaciones.txt";
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // Inicializa el archivo y recupera el contador si el archivo ya existe
    static {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
                out.println("N°\tFecha\tHora de inicio\tHora de fin\tTiempo de respuesta (ms)\tTipo de operación\tEstado\tObservación");
            } catch (IOException e) {
                logger.error("Error al crear el archivo de log de operaciones", e);
            }
        } else {
            try {
                long lines = Files.lines(path).count();
                if (lines > 0) {
                    counter.set((int) lines - 1);
                }
            } catch (IOException e) {
                logger.error("Error al leer el archivo de log de operaciones", e);
            }
        }
    }

    // Intercepta todos los métodos públicos dentro de las clases anotadas con @RestController
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Por defecto, usa "NombreControlador.nombreMetodo"
        String operacion = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        
        // Si tiene la anotación personalizada, usa ese nombre
        LogOperacion logAnnotation = method.getAnnotation(LogOperacion.class);
        if (logAnnotation != null && !logAnnotation.value().trim().isEmpty()) {
            operacion = logAnnotation.value();
        }

        String estado = "Exitosa";
        String observacion = "Respuesta dentro del rango esperado";
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            estado = "Fallida";
            observacion = "Excepción: " + e.getClass().getSimpleName() + " - " + (e.getMessage() != null ? e.getMessage() : "Sin mensaje");
            throw e; // Lanza la excepción de nuevo para que Spring la maneje normalmente
        } finally {
            long endMillis = System.currentTimeMillis();
            LocalDateTime endTime = LocalDateTime.now();
            long duration = endMillis - startMillis;

            int num = counter.incrementAndGet();
            String date = startTime.format(DATE_FORMAT);
            String startStr = startTime.format(TIME_FORMAT);
            String endStr = endTime.format(TIME_FORMAT);

            String logLine = String.format("%d\t%s\t%s\t%s\t%d\t%s\t%s\t%s",
                    num, date, startStr, endStr, duration, operacion, estado, observacion);

            writeLogToFile(logLine);
        }

        return result;
    }

    private synchronized void writeLogToFile(String logLine) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            out.println(logLine);
        } catch (IOException e) {
            logger.error("Error escribiendo en el log de operaciones", e);
        }
    }
}
