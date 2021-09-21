package fr.inserm.u1078.tludwig.privas.utils;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-12-10
 */
public class BedRegion implements Comparable<BedRegion> {

  /**
   * Start of the Region (in base 0)
   */
  private int start;
  /**
   * End of the Region (in base 1)
   */
  private int end;

  /**
   * Creates a new BedRegion
   * @param start start of the region (in base 0)
   * @param end   end of the region (in base 1)
   * Due to the mixing between base0 and base1 coordinates, variant at position 17:45 would be located on a region 17 44 45
   * @throws BedRegion.BedRegionException if end <= start
   */
  public BedRegion(int start, int end) throws BedRegionException { 
    if(end < start)
      throw new BedRegionException("Cannot create a new "+BedRegion.class.getSimpleName()+" [must have start (base-0) < end (base-1)] : here ["+start+">"+end+"]");
    if(start == end){
      this.start = start - 1 ;//Some bed files have chr   base-1   base-1
      throw new BedRegionException("Cannot create a new "+BedRegion.class.getSimpleName()+" [must have start (base-0) < end (base-1)] : here ["+start+"="+end+"] so it seems to be in base-1 base-1");
    }
    else
      this.start = start;
    this.end = end;
  }

  /**
   * Return true if the given position to contained in the BedRegion
   * @param pos the position to lookup
   * @return true if the given position to contained in the BedRegion
   */
  public boolean contains(int pos){
    return this.start < pos && pos <= this.end;
  }
  
  /**
   * Returns true if this BedRegion and r overlaps
   * @param r the other region to test
   * @return true if this BedRegion and r overlaps
   */
  public boolean overlap(BedRegion r) {
    //either this.start is in r or r.start is in this0
    if(this.start <= r.start)
      return r.start < this.end;//not <= because base-0 < base-1
    else //18 > 15
      return this.start < r.end;
  }

  /**
   * Gets the Start of the BedRegion (0-based)
   * @return the Start of the BedRegion (0-based)
   */
  public int getStart() {
    return start;
  }

  /**
   * Gets the End of the BedRegion (1-based)
   * @return  the End of the BedRegion (1-based)
   */
  public int getEnd() {
    return end;
  }

  @Override
  public int compareTo(BedRegion r) {
    int cmp = this.start - r.start;
    return cmp == 0 ? this.end - r.end : cmp;
  }
  
  @Override
  public String toString(){
    return "R]"+start+";"+end+"]";
  }

  /**
   * extends this BedRegion to its union with BedRegion r
   * @param r the other region
   * @throws BedRegion.BedRegionException if this BedRegion and r do not overlap
   */
  public void applyUnion(BedRegion r) throws BedRegionException {
    if(!this.overlap(r))
      throw new BedRegionException("Cannot get union between "+this+" and "+r+", regions do not overlap");
    this.start = Math.min(this.start, r.start);
    this.end = Math.max(this.end, r.end);
  }
  
  /**
   * limits this BedRegion to its intersection with BedRegion r
   * @param r the other region
   * @throws BedRegion.BedRegionException if this BedRegion and r do not overlap
   */
  @SuppressWarnings("unused")
  public void applyIntersection(BedRegion r) throws BedRegionException{
    if(!this.overlap(r))
      throw new BedRegionException("Cannot get intersection between "+this+" and "+r+", regions do not overlap");
    this.start = Math.max(this.start, r.start);
    this.end = Math.min(this.end, r.end);
  }  
  
  /**
   * Returns a BedRegion that is the union of BedRegions r1 and r2
   * @param r1 the first region
   * @param r2 the section region
   * @return the union of r1 and r2
   * @throws BedRegion.BedRegionException if r1 and r2 do not overlap
   */
  @SuppressWarnings("unused")
  public static BedRegion getUnion(BedRegion r1, BedRegion r2) throws BedRegionException {
    if(!r1.overlap(r2))
      throw new BedRegionException("Cannot get union between "+r1+" and "+r2+", regions do not overlap");
    int start = Math.min(r1.start, r2.start);
    int end = Math.max(r1.end, r2.end);
    return new BedRegion(start, end);
  }
  
  /**
   * Returns a BedRegion that is the intersection of BedRegions r1 and r2
   * @param r1 the first region
   * @param r2 the section region
   * @return the intersection of r1 and r2
   * @throws BedRegion.BedRegionException if r1 and r2 do not overlap
   */
  public static BedRegion getIntersection(BedRegion r1, BedRegion r2) throws BedRegionException {
    if(!r1.overlap(r2))
      throw new BedRegionException("Cannot get union between "+r1+" and "+r2+", regions do not overlap");
    int start = Math.max(r1.start, r2.start);
    int end = Math.min(r1.end, r2.end);
    return new BedRegion(start, end);
  }
  
  /**
   * Exception related to Bed Regions
   */
  public static class BedRegionException extends Exception {
    public BedRegionException(String s){
      super(s);
    }
    
    public BedRegionException(String s, Throwable t) {
      super(s, t);
    }
  }
}
