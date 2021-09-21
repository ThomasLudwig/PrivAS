package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.MSG;
import fr.inserm.u1078.tludwig.privas.utils.BedRegion.BedRegionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-12-10
 */
public class BedFile implements Cloneable {

  private final HashMap<Integer, ArrayList<BedRegion>> regions;

  public BedFile() {
    this.regions = new HashMap<>();
  }

  public BedFile(String filename) throws IOException, BedRegionException {
    this();
    if(filename != null && !filename.isEmpty())
      load(filename);
  }
  
  public boolean isEmpty(){
    return regions.isEmpty();
  }

  public void load(String filename) throws IOException, BedRegionException {
    UniversalReader in = new UniversalReader(filename);
    String line;
    while ((line = in.readLine()) != null)
      this.add(line);
    in.close();
  }

  /**
   * Adds a bed line region
   *
   * @param line a string in the bed format (starts with chrom<TAB>0-based-Start<TAB>1-based-end)
   * @throws BedRegion.BedRegionException if in the line cannot be parsed or if end <= start
   */
  public void add(String line) throws BedRegionException {
    String[] f = line.split("\\s+");
    try {
      int chr = CanonicalVariant.getChrAsNumber(f[0]);
      int start = new Integer(f[1]);
      int end = new Integer(f[2]);
      this.add(chr, new BedRegion(start, end));
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new BedRegionException(MSG.cat(MSG.BED_UNABLE_PARSE, line), e);
    }
  }

  /**
   * Adds to the bedfile and merges (Union) with all overlapping regions
   *
   * @param chr the target chromosome
   * @param r the region to add
   */
  public void add(int chr, BedRegion r) {
    ArrayList<BedRegion> chromosome = this.regions.computeIfAbsent(chr, k -> new ArrayList<>());

    //1) add region
    int idx = add(chromosome, r);
    //2) if intersect next, applyIntersection to current and remove next
    if (idx < chromosome.size() - 1) {
      BedRegion next = chromosome.get(idx + 1);
      if (r.overlap(next))
        try {
          r.applyUnion(chromosome.remove(idx + 1));
        } catch(BedRegionException ignore){
          //cannot happen since overlap is test above
        }
    }
    //3) if intersect prev, applyIntersection to previous and remove current
    if (idx > 0) {
      BedRegion prev = chromosome.get(idx - 1);
      if (prev.overlap(r))
        try {
          prev.applyUnion(chromosome.remove(idx));
        } catch(BedRegionException ignore){
          //cannot happen since overlap is test above
        }
    }
  }

  /**
   * Adds to a position and return index
   *
   * @param list ArrayList of Regions
   * @param r the BedRegion to add
   * @return the index of r in list
   */
  private static int add(ArrayList<BedRegion> list, BedRegion r) {
    //Don't do an insertion sort.
    //Logically, a Bed file is already sorted, so we scan from the end, complexity c should be [o(1) ; o(log(n))]
    for (int i = list.size() - 1; i >= 0; i--)
      if (list.get(i).compareTo(r) <= 0) {
        list.add(i + 1, r);
        return i + 1;
      }
    list.add(0, r);
    return 0;
  }

  /**
   * Adds all the Regions from the given bed file to this bed file (this bed file commons its union with the given bed file)
   *
   * @param bed the BedFile to add
   */
  public void addAsUnion(BedFile bed) {
    for (int chr : bed.regions.keySet())
      for (BedRegion r : bed.regions.get(chr))
        this.add(chr, r);
  }

  /**
   * Tells if a variant (in canonical format) overlaps at least one region in the bed file
   *
   * @param canonicalVariant the variant to test
   * @return if a variant (in canonical format) overlaps at least one region in the bed file
   */
  public boolean overlaps(CanonicalVariant canonicalVariant) {
    return this.overlap(canonicalVariant.getChrom(), canonicalVariant.getPos(), canonicalVariant.getLength());
  }

  /**
   * Tells of a given position is covered by a bed file
   *
   * @param chr the chromosome of the region to test
   * @param pos the position of the region to test
   * @return true if this BedFile contains the given position
   */
  @SuppressWarnings("unused")
  public boolean contains(int chr, int pos) {
    ArrayList<BedRegion> chromosome = this.regions.get(chr);
    if (chromosome == null)
      return false;

    for (BedRegion reg : chromosome) //o(N), could be o(log(n))
      if (reg.contains(pos))
        return true;
    return false;
  }

  /**
   * Tells if a position and length overlaps at least one region in the bed file
   *
   * @param chr the chromosome of the region to test
   * @param pos the start of the region to test
   * @param length the length of the region to test
   * @return true of the described region overlaps at least one region from this BedFile
   */
  public boolean overlap(int chr, int pos, int length)  {
    ArrayList<BedRegion> chromosome = this.regions.get(chr);
    if (chromosome == null)
      return false;

    try {
      BedRegion r = new BedRegion(pos - 1, pos + length);
      //Get the least region with start before this start
      int f = 0;
      int l = chromosome.size();
      int c;
      while (l - f > 1) {
        c = (f + l) / 2;
        if (chromosome.get(c).getStart() < pos - 1)
          f = c;
        else
          l = c;
      }

      if (chromosome.get(f).overlap(r))
        return true;
      if (f < chromosome.size() - 2)
        return chromosome.get(f + 1).overlap(r);
    } catch(BedRegionException ignore){
      //cannot happen since pos - 1 < pos + length, as length >= 0
    }
    return false;
  }

  /*
  public void print(){
    for(int chr : this.regions.keySet())
      for(BedRegion r : this.regions.get(chr))
        System.out.println(chr+" "+r.toString());
  }
  */

  /**
   * Produces the intersection between two bed files. If one bed file is completely empty, and returns the other one.
   * @param bedA  first bed file
   * @param bedB  second bed file
   * @return a BedFile that is the intersection of the two input BedFiles
   */
  public static BedFile getIntersection(BedFile bedA, BedFile bedB) {
    BedFile intersect = new BedFile();
    if(bedA.isEmpty())
      return bedB.copy();
    if(bedB.isEmpty())
      return bedA.copy();
    
    for (int chr : bedA.regions.keySet())
      if (bedB.regions.containsKey(chr)) {
        ArrayList<BedRegion> rsA = bedA.regions.get(chr);
        ArrayList<BedRegion> rsB = bedB.regions.get(chr);
        //for each rA in rsA, we look for every rBs in rsB that overlap rA
        //by default this would be in o(N*M), but as rsA and rsB are both sorted its easier
        //we look for the index iS of the first and the index iE  of the last position rB bordering rA
        //we add the intersection of rA/rB(iS to iE)
        //the sorting guarantees that iS(rA) <= iS(rA+1) and iS(rB) <= iS(rB+1)

        int iS = 0;
        int iE = 0;
        for (BedRegion rA : rsA) {
          //increment iS until [rB(iS).startBefore(rA)] and !rB(iS+1).startBefore(rA)
          //special cases iS == rsB.size()-1

          //we look at the last that starts before rA (or the first starts after, if not found)
          //iS = 0, if IS+1 before, IS++ -> IS+1 exists only if IS < size-2
          while (iS < rsB.size()-1 && rsB.get(iS + 1).getStart() <= rA.getStart())
            iS++;
          
          //set iE=max(iS,iE)
          if (iS > iE)
            iE = iS;
          //increment iE until rB(iS).endsAfter(rA) [don't have to check : and !rB(iS-1).endsAfter(rA)]
          //special cases iE == rsB.size()-1
          //we look at the first that ends after rA (or the last that ends before, if not found)
          while (iE < rsB.size()-1 && rsB.get(iE).getEnd() < rA.getEnd())
            iE++;

          for(int i = iS; i <= iE; i++)
            if(rA.overlap(rsB.get(i)))
              try {
                intersect.add(chr, BedRegion.getIntersection(rA, rsB.get(i)));
              } catch(BedRegionException ignore){
                //cannot happen since overlap is tested above
              }
          
        }
      }
    return intersect;
  }
  
  public BedFile copy(){
    try {
      return (BedFile)this.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }    
  }
  
  @Override
  public Object clone() throws CloneNotSupportedException{
    return super.clone();
  }

  @SuppressWarnings("unused")
  public static BedFile getUnion(BedFile bed1, BedFile bed2) {
    BedFile union = new BedFile();
    union.addAsUnion(bed1);
    union.addAsUnion(bed2);
    return union;
  }

  public static BedFile deserialize(String s) throws BedRegionException {
    BedFile bed = new BedFile();
    if(s != null && !s.isEmpty()){
      String[] chroms = s.split(";");
      for (String chrom : chroms) {
        String[] f = chrom.split(":");
        int chr = new Integer(f[0]);
        ArrayList<BedRegion> regions = new ArrayList<>();
        for (String pos : f[1].split(",")) {
          String[] p = pos.split("-");
          regions.add(new BedRegion(new Integer(p[0]), new Integer(p[1])));
        }
        bed.regions.put(chr, regions);
      }
    }
    return bed;
  }

  public String serialize() {
    StringBuilder sb = new StringBuilder();
    for (int chr : this.regions.keySet()) {
      sb.append(";").append(chr).append(":");
      boolean next = false;
      for (BedRegion region : this.regions.get(chr)) {
        if (next)
          sb.append(",");
        sb.append(region.getStart()).append("-").append(region.getEnd());
        next = true;
      }
    }
    if (sb.length() > 0)
      return sb.substring(1);
    return "";
  }
}
