package com.boris.exeption;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {
}
