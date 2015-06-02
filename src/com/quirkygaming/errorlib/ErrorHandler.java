package com.quirkygaming.errorlib;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;

public class ErrorHandler<T extends Exception> {
	
	private HashSet<Class<?>> handledExceptions = new HashSet<Class<?>>();
	private HashSet<Class<?>> unwrappedExceptions = new HashSet<Class<?>>();
	private PrintStream log;
	
	public static ErrorHandler<Exception> forwardHandled(PrintStream log, Class<?>... handledExceptions) {
		return new ErrorHandler<Exception>(log, false, Exception.class, handledExceptions);
	}
	public static ErrorHandler<RuntimeException> logAll(PrintStream log) {
		return new ErrorHandler<RuntimeException>(log, false, RuntimeException.class);
	}
	public static ErrorHandler<Exception> forwardHandled(Class<?>... handledExceptions) {
		return new ErrorHandler<Exception>(null, false, Exception.class, handledExceptions);
	}
	public static ErrorHandler<RuntimeException> throwAll() {
		return new ErrorHandler<RuntimeException>(null, true, RuntimeException.class);
	}
	
	private ErrorHandler(PrintStream log, boolean bypassMode, Class<T> mode, Class<?>... handledExceptions) {
		this.log = log;
		Collections.addAll(this.handledExceptions, handledExceptions);
	}
	
	public ErrorHandler<T> addHandledExceptions(Class<?>... exceptionTypes) {
		Collections.addAll(handledExceptions, exceptionTypes);
		return this;
	}
	
	public ErrorHandler<T> addExceptionsToUnwrap(Class<?>... exceptionTypes) {
		Collections.addAll(unwrappedExceptions, exceptionTypes);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void handle(Exception e) throws T {
		Throwable t = e;
		if (unwrappedExceptions.contains(e.getClass())) {
			t = e.getCause();
		}
		if (handledExceptions.contains(t.getClass())) {
			throw (T)t;
		} else {
			if (log == null) {
				if (t instanceof RuntimeException) {
					throw (RuntimeException)t;
				} else {
					throw new RuntimeException(t);
				}
			} else {
				StackTraceElement ste = e.getStackTrace()[0];
				log.println("Caught " + e.getClass().getName() + ": " + e.getMessage() + " at line " + ste.getLineNumber() + " in " + ste.getClassName());
			}
		}
	}
}
