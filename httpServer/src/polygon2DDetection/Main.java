package polygon2DDetection;

import java.util.ArrayList;
import java.util.List;
import vector2D.Position;
import vector2D.Vector;

/**
 * Testing class
 * @author Quentin
 *
 */
public class Main 
{
	public final static double APROXIMATION = 0;
	
	public static void main(String[] args)
	{
		/* Hearth :
			Position a = new Position(44.912198,4.916489
			Position a = new Position(44.911712,4.916039
			Position a = new Position(44.911666,4.915674
			Position a = new Position(44.911119,4.915717
			Position a = new Position(44.91045,4.916382
			Position a = new Position(44.910314,4.916811
			Position a = new Position(44.909675,4.917541
			Position a = new Position(44.909691,4.91812
			Position a = new Position(44.909128,4.918742
			Position a = new Position(44.910131,4.919364
			Position a = new Position(44.910291,4.919171
			Position a = new Position(44.911423,4.920008
			Position a = new Position(44.912145,4.918195
			Position a = new Position(44.911537,4.917927
		 */
		
		Position a = new Position(1,1);
		Position b = new Position(4,1);
		Position c = new Position(2,4);
		Position d = new Position(2,2);
		Position e = new Position(5,2);
		Position f = new Position(3,5);
		
		Position c1 = new Position(44.912198,4.916489);
		Position c2= new Position(44.911712,4.916039);
		Position c3 = new Position(44.911666,4.915674);
		Position c4= new Position(44.911119,4.915717);
		Position c5= new Position(44.91045,4.916382);
		Position c6= new Position(44.910314,4.916811);
		Position c7= new Position(44.909675,4.917541);
		Position c8= new Position(44.909691,4.91812);
		Position c9 = new Position(44.909128,4.918742);
		Position c10= new Position(44.910131,4.919364);
		Position c11= new Position(44.910291,4.919171);
		Position c12= new Position(44.911423,4.920008);
		Position c13= new Position(44.912145,4.918195);
		Position c14= new Position(44.911537,4.917927);
		
		Vector vh1 = new Vector(c1,c2);
		Vector vh2 = new Vector(c2,c3);
		Vector vh3 = new Vector(c3,c4);
		Vector vh4 = new Vector(c4,c5);
		Vector vh5 = new Vector(c5,c6);
		Vector vh6 = new Vector(c6,c7);
		Vector vh7 = new Vector(c7,c8);
		Vector vh8 = new Vector(c8,c9);
		Vector vh9 = new Vector(c9,c10);
		Vector vh10= new Vector(c10,c11);
		Vector vh11= new Vector(c11,c12);
		Vector vh12= new Vector(c12,c13);
		Vector vh13= new Vector(c13,c14);
		Vector vh14= new Vector(c14,c1);
		
		
		Vector v1 = new Vector(a,b); 
		Vector v2 = new Vector(b,c);
		Vector v3 = new Vector(c,a);
		
		ArrayList<Vector> l1 = new ArrayList<Vector>();
		List<Position> p = new ArrayList<Position>();
		
		ArrayList<Vector> vcoeur = new ArrayList<Vector>();
		
		Polygon triangle1 = null, coeur = null;
		
		l1.add(v1);
		l1.add(v2);
		l1.add(v3);
		
		p.add(a);
		p.add(b);
		p.add(c);
		p.add(d);
		p.add(e);
		p.add(f);
		p.add(c1);
		p.add(c2);
		p.add(c3);
		p.add(c4);
		p.add(c5);
		p.add(c6);
		p.add(c7);
		p.add(c8);
		p.add(c9);
		p.add(c10);
		p.add(c11);
		p.add(c12);
		p.add(c13);
		p.add(c14);
		
		vcoeur.add(vh1);
		vcoeur.add(vh2);
		vcoeur.add(vh3);
		vcoeur.add(vh4);
		vcoeur.add(vh5);
		vcoeur.add(vh6);
		vcoeur.add(vh7);
		vcoeur.add(vh8);
		vcoeur.add(vh9);
		vcoeur.add(vh10);
		vcoeur.add(vh11);
		vcoeur.add(vh12);
		vcoeur.add(vh13);
		vcoeur.add(vh14);
	
		try
		{
			triangle1 = new Polygon("triangle1", l1);
			coeur = new Polygon("coeur", vcoeur);
		}
		catch (NotPolygonException exception)
		{
			System.out.println("Erreur cr�ation du triangle");
		}
		
		System.out.println("Forme � d�tecter : \n\t"+triangle1.toString());
		System.out.println("Nuage de point : \n\t"+p.toString());
		System.out.println("R�sultat : \n\t"+PolygonDetection.isTherePolygon(p, triangle1, APROXIMATION).toString());
		
		System.out.println("Forme � d�tecter : \n\t"+coeur.toString());
		ArrayList<ArrayList<Position>> franck = PolygonDetection.isTherePolygon(p, coeur, APROXIMATION);
		System.out.println("R�sultat : \n\t"+franck.toString());
		System.out.println(franck.size());
	}
}
