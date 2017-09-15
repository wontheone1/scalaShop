package scalashop

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._

@RunWith(classOf[JUnitRunner])
class BlurSuite extends FunSuite {
  test("boxBlurKernel should correctly handle radius 0") {
    val src = new Img(5, 5)

    for (x <- 0 until 5; y <- 0 until 5)
      src(x, y) = rgba(x, y, x + y, math.abs(x - y))

    for (x <- 0 until 5; y <- 0 until 5)
      assert(boxBlurKernel(src, x, y, 0) === rgba(x, y, x + y, math.abs(x - y)),
        "boxBlurKernel(_,_,0) should be identity.")
  }

  test("boxBlurKernel should return the correct value on an interior pixel " +
    "of a 3x4 image with radius 1") {
    val src = new Img(3, 4)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8
    src(0, 3) = 50; src(1, 3) = 11; src(2, 3) = 16

    assert(boxBlurKernel(src, 1, 2, 1) === 12,
      s"(boxBlurKernel(1, 2, 1) should be 12, " +
        s"but it's ${boxBlurKernel(src, 1, 2, 1)})")
  }

  test("HorizontalBoxBlur.blur with radius 1 should correctly blur the entire 3x3 image") {
    val w = 3
    val h = 3
    val src = new Img(w, h)
    val sequentialDest = new Img(w, h)
    val parallelDest = new Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8

    HorizontalBoxBlur.blur(src, sequentialDest, 0, 2, 1)
    HorizontalBoxBlur.blur(src, parallelDest, 0, 2, 1)

    def check(x: Int, y: Int, dest: Img, expected: Int) =
      assert(dest(x, y) == expected,
        s"(destination($x, $y) should be $expected)")

    check(0, 0, sequentialDest, 2)
    check(0, 0, parallelDest, 2)
    check(1, 0, sequentialDest, 2)
    check(1, 0, parallelDest, 2)
    check(2, 0, sequentialDest, 3)
    check(2, 0, parallelDest, 3)
    check(0, 1, sequentialDest, 3)
    check(0, 1, parallelDest, 3)
    check(1, 1, sequentialDest, 4)
    check(1, 1, parallelDest, 4)
    check(2, 1, sequentialDest, 4)
    check(2, 1, parallelDest, 4)
    check(0, 2, sequentialDest, 0)
    check(0, 2, parallelDest, 0)
    check(1, 2, sequentialDest, 0)
    check(1, 2, parallelDest, 0)
    check(2, 2, sequentialDest, 0)
    check(2, 2, parallelDest, 0)
  }

  test("VerticalBoxBlur.blur and parBlur with radius 2 should correctly blur the entire " +
    "4x3 image") {
    val w = 4
    val h = 3
    val src = new Img(w, h)
    val sequentialDest = new Img(w, h)
    val parallelDest = new Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2; src(3, 0) = 9
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5; src(3, 1) = 10
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8; src(3, 2) = 11

    VerticalBoxBlur.blur(src, sequentialDest, 0, 4, 2)
    VerticalBoxBlur.parBlur(src, parallelDest, 2, 2)

    def check(x: Int, y: Int, dest: Img, expected: Int) =
      assert(dest(x, y) == expected,
        s"(destination($x, $y) should be $expected)")

    check(0, 0, sequentialDest, 4)
    check(0, 0, parallelDest, 4)
    check(1, 0, sequentialDest, 5)
    check(1, 0, parallelDest, 5)
    check(2, 0, sequentialDest, 5)
    check(2, 0, parallelDest, 5)
    check(3, 0, sequentialDest, 6)
    check(3, 0, parallelDest, 6)
    check(0, 1, sequentialDest, 4)
    check(0, 1, parallelDest, 4)
    check(1, 1, sequentialDest, 5)
    check(1, 1, parallelDest, 5)
    check(2, 1, sequentialDest, 5)
    check(2, 1, parallelDest, 5)
    check(3, 1, sequentialDest, 6)
    check(3, 1, parallelDest, 6)
    check(0, 2, sequentialDest, 4)
    check(0, 2, parallelDest, 4)
    check(1, 2, sequentialDest, 5)
    check(1, 2, parallelDest, 5)
    check(2, 2, sequentialDest, 5)
    check(2, 2, parallelDest, 5)
    check(3, 2, sequentialDest, 6)
    check(3, 2, parallelDest, 6)
  }


}
