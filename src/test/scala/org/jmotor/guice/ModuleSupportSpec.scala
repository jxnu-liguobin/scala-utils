package org.jmotor.guice

import com.google.inject.Guice
import com.google.inject.TypeLiteral
import com.typesafe.config.ConfigFactory
import org.jmotor.guice.service.Conf
import org.jmotor.guice.service.ConfigService
import org.jmotor.guice.service.FooService
import org.jmotor.guice.service.MultiConfigService
import org.jmotor.guice.service.MultiPingService
import org.jmotor.guice.service.PingService
import org.jmotor.guice.service.impls.LongConfigService
import org.jmotor.guice.service.impls.StringConfigService
import org.scalatest.funsuite.AnyFunSuite

/**
 * Component:
 * Description:
 * Date: 2017/4/11
 *
 * @author AI
 */
class ModuleSupportSpec extends AnyFunSuite {

  test("Bind components") {
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindComponents("org.jmotor.guice.service.impl")
    })
    val service = injector.getInstance(classOf[PingService])
    assert(service.ping() == "pong")
  }

  test("Test bind extension") {
    val config = ConfigFactory.parseString("extension.enabled = true")
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindExtendableComponents("org.jmotor.guice.service.impl", config)
    })
    val service = injector.getInstance(classOf[PingService])
    assert(service.ping() == "extension pong")
  }

  test("Test bind extension package") {
    val config = ConfigFactory.parseString("extension.enabled = true")
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindExtendableComponents("org.jmotor.guice.service.impl", "org.jmotor.guice.service.v2", config)
    })
    val service = injector.getInstance(classOf[PingService])
    assert(service.ping() == "extension pong v2")
  }

  test("Test bind multi") {
    val injector = Guice.createInjector(new AbstractMultiModuleSupport {
      override def configure(): Unit = bindMultiComponent[PingService]("org.jmotor.guice.service.impls")
    })
    val pings = injector.getInstance(classOf[MultiPingService]).pings
    assert(pings.contains("v1"))
    assert(pings.contains("v2"))
  }

  test("Test bind multi generic") {
    val injector = Guice.createInjector(new AbstractMultiModuleSupport {
      override def configure(): Unit = bindMultiComponent[ConfigService[Conf]](new TypeLiteral[ConfigService[Conf]]() {}, "org.jmotor.guice.service.impls")
    })
    val impls = injector.getInstance(classOf[MultiConfigService]).impls
    assert(impls.exists(_.isInstanceOf[StringConfigService]))
    assert(impls.exists(_.isInstanceOf[LongConfigService]))
  }

  test("Test extends extension") {
    val config = ConfigFactory.parseString("extension.enabled = true")
    val injector = Guice.createInjector(new AbstractModuleSupport {
      override def configure(): Unit = bindExtendableComponents("org.jmotor.guice.service.impl", config)
    })
    val service = injector.getInstance(classOf[FooService])
    assert("run" == service.run())
    assert("invoke" == service.call())
  }

}
