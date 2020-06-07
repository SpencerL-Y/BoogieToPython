procedure main() returns () {
    var x0, x1: real;
    while (x0*x0 + x1*x1 <=1) {
        x0, x1 := x0 - x1*x1 +1, x1 + x0*x0 -1;
    }
}
