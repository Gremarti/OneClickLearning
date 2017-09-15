

package utils;
import fr.insa.ocm.model.utils.Vector;
import org.junit.Before;
import org.junit.Test;

import static fr.insa.ocm.model.utils.Vector.scalarProduct;
import static org.junit.Assert.*;


public class TestVector {
	private double[] valvect1 = {1,2,3};
	private double[] valvect2 = {4,5,6};
	private Vector vect1;
	private Vector vect2;
	private Vector vect3;

	@Before
	public void initData(){
		vect1 = new Vector(valvect1);
		vect2 = new Vector(valvect2);
		vect3 = new Vector();
	}

	@Test
	public void testGetValues() {
		assertArrayEquals(valvect1,vect1.getValues(),0.0);
		assertArrayEquals(valvect2,vect2.getValues(),0.0);
		assertArrayEquals(new double[0],vect3.getValues(),0.0);
	}

	@Test
	public void testPut() {
		double[] valvectFinal = {1,2,3,4,5,6};
		vect1.put(vect2);
		assertArrayEquals(valvectFinal,vect1.getValues(),0.0);
	}
	@Test
	public void testNorm() {
		try{
			double norm = vect1.norm(2);
			System.out.println(vect1);
			assertEquals(Math.sqrt(14.0),norm,0.0);
		}catch (ArithmeticException exp){
			System.out.println(exp);
		}
	}
		@Test
		public void testScalarProduct() {
		double scalar = scalarProduct(vect1,vect2);
		assertEquals(32.0,scalar,0.0);
	}
	@Test
	public void testEquals() {
		Vector vect1Bis = new Vector(valvect1);
		assertTrue(vect1.equals(vect1Bis));
	}
}

