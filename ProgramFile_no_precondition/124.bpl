// #Termination
/* 
 * Date: 2014-07-30 
 * Author: heizmann@informatik.uni-freiburg.de
 */

function myFunc(in: int) returns (res : bool);
var x: int;

procedure main() returns ()
modifies x;
{
  while (myFunc(x)) {
    x := x + 1;
  }
}
