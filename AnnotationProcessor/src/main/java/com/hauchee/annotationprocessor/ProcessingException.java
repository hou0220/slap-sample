package com.hauchee.annotationprocessor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class ProcessingException extends Exception {

    private final Element element;

    private final AnnotationMirror annotationMirror;

    public ProcessingException(Element element, AnnotationMirror annotationMirror, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
        this.annotationMirror = annotationMirror;
    }

    public ProcessingException(Element element, String msg, Object... args) {
        this(element, null, msg, args);
    }

    public Element getElement() {
        return element;
    }

    public AnnotationMirror getAnnotationMirror() {
        return annotationMirror;
    }
}
