package top.ggv.utils;

//http://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples

public class Pair<L,R> implements java.io.Serializable 
{

	// final enlevé !
	  private L left;
	  private R right;

	  public Pair(L left, R right) {
	    this.left = left;
	    this.right = right;
	  }

	  public L getLeft() { return left; }
	  public R getRight() { return right; }

	  @Override
	  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

	  @Override
	  public boolean equals(Object o) {
	    if (o == null) return false;
	    if (!(o instanceof Pair)) return false;
	    Pair pairo = (Pair) o;
	    return this.left.equals(pairo.getLeft()) &&
	           this.right.equals(pairo.getRight());
	  }

	  
	  @Override
	  public String toString() { return left.toString() +":" + right.toString(); }
	  
	  public void setLeft(L _left) { left=_left;}
	  public void setRight(L _right) { left=_right;}
	  
	}