package fj;

/**
 * A transformation function of arity-5 from <code>A</code>, <code>B</code>, <code>C</code>,
 * <code>D</code> and <code>E</code> to <code>F$</code>. This type can be represented using the Java
 * 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 413 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:31:32 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
public abstract class F5<A, B, C, D, E, F$> {
  /**
   * Transform <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code> and <code>E</code> to
   * <code>F$</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @param d The <code>D</code> to transform.
   * @param e The <code>E</code> to transform.
   * @return The result of the transformation.
   */
  public abstract F$ f(A a, B b, C c, D d, E e);
}
