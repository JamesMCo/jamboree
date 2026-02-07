package uk.mrjamesco.jamboree

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker

class PrefixedLogger(name: String): Logger {
    private val logger: Logger = LoggerFactory.getLogger(name)

    override fun getName(): String? = logger.name

    override fun isTraceEnabled(): Boolean                      = logger.isTraceEnabled
    override fun trace(msg: String?)                            = logger.trace("[$name] $msg", name, msg)
    override fun trace(format: String?, arg: Any?)              = logger.trace("[{}] $format", name, arg)
    override fun trace(format: String?, arg1: Any?, arg2: Any?) = logger.trace("[{}] $format", name, arg1, arg2)
    override fun trace(format: String?, vararg arguments: Any?) = logger.trace("[{}] $format", name, *arguments)
    override fun trace(msg: String?, t: Throwable?)             = logger.trace("[$name] $msg", t)

    override fun isTraceEnabled(marker: Marker?): Boolean                        = logger.isTraceEnabled(marker)
    override fun trace(marker: Marker?, msg: String?)                            = logger.trace(marker, "[$name] $msg")
    override fun trace(marker: Marker?, format: String?, arg: Any?)              = logger.trace(marker, "[{}] $format", name, arg)
    override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.trace(marker, "[{}] $format", name, arg1, arg2)
    override fun trace(marker: Marker?, format: String?, vararg arguments: Any?)  = logger.trace(marker, "[{}] $format", name, *arguments)
    override fun trace(marker: Marker?, msg: String?, t: Throwable?)             = logger.trace(marker, "[$name] $msg", t)

    override fun isDebugEnabled(): Boolean                      = logger.isDebugEnabled
    override fun debug(msg: String?)                            = logger.debug("[$name] $msg", name, msg)
    override fun debug(format: String?, arg: Any?)              = logger.debug("[{}] $format", name, arg)
    override fun debug(format: String?, arg1: Any?, arg2: Any?) = logger.debug("[{}] $format", name, arg1, arg2)
    override fun debug(format: String?, vararg arguments: Any?) = logger.debug("[{}] $format", name, *arguments)
    override fun debug(msg: String?, t: Throwable?)             = logger.debug("[$name] $msg", t)

    override fun isDebugEnabled(marker: Marker?): Boolean                        = logger.isDebugEnabled(marker)
    override fun debug(marker: Marker?, msg: String?)                            = logger.debug(marker, "[$name] $msg")
    override fun debug(marker: Marker?, format: String?, arg: Any?)              = logger.debug(marker, "[{}] $format", name, arg)
    override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.debug(marker, "[{}] $format", name, arg1, arg2)
    override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) = logger.debug(marker, "[{}] $format", name, *arguments)
    override fun debug(marker: Marker?, msg: String?, t: Throwable?)             = logger.debug(marker, "[$name] $msg", t)

    override fun isInfoEnabled(): Boolean                      = logger.isInfoEnabled
    override fun info(msg: String?)                            = logger.info("[$name] $msg", name, msg)
    override fun info(format: String?, arg: Any?)              = logger.info("[{}] $format", name, arg)
    override fun info(format: String?, arg1: Any?, arg2: Any?) = logger.info("[{}] $format", name, arg1, arg2)
    override fun info(format: String?, vararg arguments: Any?) = logger.info("[{}] $format", name, *arguments)
    override fun info(msg: String?, t: Throwable?)             = logger.info("[$name] $msg", t)

    override fun isInfoEnabled(marker: Marker?): Boolean                        = logger.isInfoEnabled(marker, )
    override fun info(marker: Marker?, msg: String?)                            = logger.info(marker, "[$name] $msg")
    override fun info(marker: Marker?, format: String?, arg: Any?)              = logger.info(marker, "[{}] $format", name, arg)
    override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.info(marker, "[{}] $format", name, arg1, arg2)
    override fun info(marker: Marker?, format: String?, vararg arguments: Any?) = logger.info(marker, "[{}] $format", name, *arguments)
    override fun info(marker: Marker?, msg: String?, t: Throwable?)             = logger.info(marker, "[$name] $msg", t)

    override fun isWarnEnabled(): Boolean                      = logger.isWarnEnabled
    override fun warn(msg: String?)                            = logger.warn("[$name] $msg", name, msg)
    override fun warn(format: String?, arg: Any?)              = logger.warn("[{}] $format", name, arg)
    override fun warn(format: String?, arg1: Any?, arg2: Any?) = logger.warn("[{}] $format", name, arg1, arg2)
    override fun warn(format: String?, vararg arguments: Any?) = logger.warn("[{}] $format", name, *arguments)
    override fun warn(msg: String?, t: Throwable?)             = logger.warn("[$name] $msg", t)

    override fun isWarnEnabled(marker: Marker?): Boolean                        = logger.isWarnEnabled(marker, )
    override fun warn(marker: Marker?, msg: String?)                            = logger.warn(marker, "[$name] $msg")
    override fun warn(marker: Marker?, format: String?, arg: Any?)              = logger.warn(marker, "[{}] $format", name, arg)
    override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.warn(marker, "[{}] $format", name, arg1, arg2)
    override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) = logger.warn(marker, "[{}] $format", name, *arguments)
    override fun warn(marker: Marker?, msg: String?, t: Throwable?)             = logger.warn(marker, "[$name] $msg", t)

    override fun isErrorEnabled(): Boolean                      = logger.isErrorEnabled
    override fun error(msg: String?)                            = logger.error("[$name] $msg", name, msg)
    override fun error(format: String?, arg: Any?)              = logger.error("[{}] $format", name, arg)
    override fun error(format: String?, arg1: Any?, arg2: Any?) = logger.error("[{}] $format", name, arg1, arg2)
    override fun error(format: String?, vararg arguments: Any?) = logger.error("[{}] $format", name, *arguments)
    override fun error(msg: String?, t: Throwable?)             = logger.error("[$name] $msg", t)

    override fun isErrorEnabled(marker: Marker?): Boolean                        = logger.isErrorEnabled(marker, )
    override fun error(marker: Marker?, msg: String?)                            = logger.error(marker, "[$name] $msg")
    override fun error(marker: Marker?, format: String?, arg: Any?)              = logger.error(marker, "[{}] $format", name, arg)
    override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) = logger.error(marker, "[{}] $format", name, arg1, arg2)
    override fun error(marker: Marker?, format: String?, vararg arguments: Any?) = logger.error(marker, "[{}] $format", name, *arguments)
    override fun error(marker: Marker?, msg: String?, t: Throwable?)             = logger.error(marker, "[$name] $msg", t)
}
