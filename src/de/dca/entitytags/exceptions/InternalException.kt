package de.dca.entitytags.exceptions

class InternalException(text: String = "An internal error occured", innerException: Throwable? = null)
    : RuntimeException(text, innerException)