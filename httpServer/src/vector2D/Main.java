package vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing class
 * @author Quentin
 *
 */
public class Main 
{
	public final static double APROXIMATION = 2;
	
	public static void main(String[] args)
	{
		List<Position> positions = new ArrayList<Position>();
		List<PositionProportion> positions1 = new ArrayList<PositionProportion>();
		List<PositionProportion> positions2 = new ArrayList<PositionProportion>();
		
		Position pos1, pos2, pos3, pos4, pos5, posno, pos6;
		pos1 = new Position(0,0);
		pos2 = new Position(4,3);
		pos3 = new Position(3,4);
		pos4 = new Position(8,6);
		pos5 = new Position(6,4);
		pos6 = new Position(1,1);
		posno = new Position(0,9);
		
		positions.add(pos1);
		positions.add(pos2);
		positions.add(pos3);
		positions.add(pos4);
		positions.add(pos5);
		positions.add(pos6);
		positions.add(posno);
		
		Vector pos12 = new Vector(pos1, pos2);
		Vector pos1100 = new Vector(1,100);
		
		System.out.println("Start : pos1, Vector : Pos12");
		System.out.println("Pos1 : \t"+pos1.toString()+" BASE");
		System.out.println("Pos2 : \t"+pos2.toString()+" OK");
		System.out.println("Pos3 : \t"+pos3.toString()+" APROX");
		System.out.println("Pos4 : \t"+pos4.toString()+" OK");
		System.out.println("Pos5 : \t"+pos5.toString()+" NO");
		System.out.println("PosNo : "+posno.toString()+" NO");
		System.out.println("Pos12 : "+pos12.toString());
		positions1 = VectorDetection.isOnVectorLine(pos1,pos12, positions,APROXIMATION);
		System.out.println(positions1.toString());
		
		System.out.println("\nStart : pos3, Vector : Pos12");
		System.out.println("Pos1 : \t"+pos1.toString()+" APROX");
		System.out.println("Pos2 : \t"+pos2.toString()+" APROX");
		System.out.println("Pos3 : \t"+pos3.toString()+" BASE");
		System.out.println("Pos4 : \t"+pos4.toString()+" APROX");
		System.out.println("Pos5 : \t"+pos5.toString()+" APROX");
		System.out.println("PosNo : "+posno.toString()+" NO");
		positions2 = VectorDetection.isOnVectorLine(pos3,pos12, positions,APROXIMATION);
		System.out.println(positions2.toString());
		
		System.out.println("\nStart : pos1, Vector : Pos1100");
		System.out.println("Pos6 : \t"+pos6.toString()+" APROX");
		System.out.println("Pos1100 : "+pos1100.toString());
		positions2 = VectorDetection.isOnVectorLine(pos1,pos1100, positions,APROXIMATION);
		System.out.println(positions2.toString());
	}
}
