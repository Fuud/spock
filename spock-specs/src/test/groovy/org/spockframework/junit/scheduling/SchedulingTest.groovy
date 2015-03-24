package org.spockframework.junit.scheduling

import org.junit.experimental.ParallelComputer
import org.junit.runner.Computer
import org.junit.runner.Description
import org.junit.runner.JUnitCore
import org.junit.runner.notification.RunListener
import spock.lang.Specification
import spock.lang.Unroll

class SchedulingTest extends Specification {
  def "with sequential scheduler iterations and features should be executed sequentially"() {
    def listener = Mock(RunListener)
    def jUnitCore = new JUnitCore()
    jUnitCore.addListener(listener)
    when:
    jUnitCore.run(Computer.serial(), SampleTest.class);
    then:
    1 * listener.testRunStarted(_)
    then:
    1 * listener.testStarted(testDescription("test for x=1"))
    then:
    1 * listener.testFinished(testDescription("test for x=1"))
    then:
    1 * listener.testStarted(testDescription("test for x=2"))
    then:
    1 * listener.testFinished(testDescription("test for x=2"))
    then:
    1 * listener.testStarted(testDescription("test simple"))
    then:
    1 * listener.testFinished(testDescription("test simple"))
    then:
    1 * listener.testStarted(testDescription("test no unroll"))
    then:
    1 * listener.testFinished(testDescription("test no unroll"))
    then:
    1 * listener.testRunFinished(_)

    where:
    clazz << [SampleTest.class, SampleTestWithTimeout.class]
  }

  def "with parallel scheduler iterations and features should be executed concurrently"() {
    def listener = Mock(RunListener)
    def jUnitCore = new JUnitCore()
    jUnitCore.addListener(listener)
    when:
    jUnitCore.run(ParallelComputer.methods(), SampleTest.class);
    then:
    1 * listener.testRunStarted(_)
    then:
    1 * listener.testStarted(testDescription("test for x=1"))
    1 * listener.testStarted(testDescription("test for x=2"))
    1 * listener.testStarted(testDescription("test simple"))
    1 * listener.testStarted(testDescription("test no unroll"))
    0 * listener.testFailure(_)
    then:
    1 * listener.testFinished(testDescription("test for x=1"))
    1 * listener.testFinished(testDescription("test for x=2"))
    1 * listener.testFinished(testDescription("test simple"))
    1 * listener.testFinished(testDescription("test no unroll"))
    0 * listener.testFailure(_)
    then:
    1 * listener.testRunFinished(_)

    where:
    clazz << [SampleTest.class, SampleTestWithTimeout.class]
  }

  private static Description testDescription(String testName) {
    Description.createTestDescription(SampleTest.class, testName)
  }

}

class SampleTest extends Specification {

  @Unroll
  def "test for x=#x"() {
    when:
    sleep 1000
    then:
    x == x
    where:
    x | _
    1 | _
    2 | _
  }

  def "test simple"() {
    def x = 1;
    when:
    sleep 1000
    then:
    x == x
  }

  def "test no unroll"() {
    when:
    sleep 1000
    then:
    x == x
    where:
    x | _
    1 | _
    2 | _
  }
}

class SampleTestWithTimeout extends Specification {

  def setup(){
    sleep 1000
  }

  @Unroll
  def "test for x=#x"() {
    when:
    sleep 1000
    then:
    x == x
    where:
    x | _
    1 | _
    2 | _
  }

  def "test simple"() {
    def x = 1;
    when:
    sleep 1000
    then:
    x == x
  }

  def "test no unroll"() {
    when:
    sleep 1000
    then:
    x == x
    where:
    x | _
    1 | _
    2 | _
  }
}
