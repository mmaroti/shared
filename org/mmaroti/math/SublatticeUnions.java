
package org.mmaroti.math;

public class SublatticeUnions 
{
    public static int[] toVector(int gens)
    {
        int[] vector = new int[16];

        vector[0] = 2;
        for(int i = 1; i <= 15; ++i)
            vector[i] = (gens & (1<<i)) != 0 ? 2 : 0;
        
        return vector;
    }

    public static int toInt(int[] vector)
    {
        int gens = 0;
        
        for(int i = 1; i <= 15; ++i)
            if( vector[i] >= 2 )
                gens |= (1<<i);
        
        return gens;
    }

    public static int getCount(int[] vector)
    {
        int c = 0;
        
        for(int i = 1; i <= 15; ++i)
            if( vector[i] >= 2 )
                ++c;
        
        return c;
    }
    
    public static String toString(int[] vector)
    {
        String s = new String();
        
        for(int i = 1; i <= 15; ++i)
            if( vector[i] >= 2 )
                s += '1';
            else
                s += '0';

        return s;
    }
    
    public static void complement(int[] vector)
    {
        for(int i = 0; i <= 15; ++i)
            if( vector[i] >= 2 )
                vector[i] = 0;
            else
                vector[i] = 2;
    }
    
    public static void cleanVector(int[] vector)
    {
        for(int i = 0; i <= 15; ++i)
            if( vector[i] >= 2 )
                vector[i] = 2;
            else
                vector[i] = 0;
    }

    public static boolean isSubuniverse(int[] vector)
    {
        cleanVector(vector);
        
        for(int i = 1; i <= 14; ++i)
            if( vector[i] >= 2 )
                for(int j = i+1; j <= 15; ++j)
                    if( vector[j] >= 2 )
                    {
                        if( ++vector[i^j] == 2 )
                            return false;
                    }

        return true;
    }

    public static void generateSubuniverse(int[] vector)
    {
        boolean updated = true;
        while( updated )
        {
            updated = false;
            cleanVector(vector);
            
            for(int i = 1; i <= 14; ++i)
                if( vector[i] >= 2 )
                    for(int j = i+1; j <= 15; ++j)
                        if( vector[j] >= 2 )
                        {
                            if( ++vector[i^j] == 2 )
                                updated = true;
                        }
        }
    }
    
    public static int getCount(int gens)
    {
        int c = 0;
        for(int i = 0; i <= 15; ++i)
            if( (gens & (1<<i)) != 0 )
                ++c;
           
        return c;
    }
    
    public static void main2(String[] args)
    {
        System.out.println("start");
        for(int i = 0; i <= 65535; i += 2)
        {
            int[] v = toVector(i);
            if( getCount(v) >= 8 && isSubuniverse(v) )
                System.out.println(toString(v));
        }
        System.out.println("done");
    }

    public static void main(String[] args)
    {
        System.out.println("start");
        for(int i = 0; i <= 65535; i += 2)
        {
            int[] v = toVector(i);
            if( getCount(v) >= 8 && isSubuniverse(v) )
            {
                System.out.print(toString(v) + " ");
                complement(v);
                generateSubuniverse(v);
                System.out.println(toString(v));
            }
        }
        System.out.println("done");
    }

    public static void main3(String[] args)
    {
        System.out.println("start");
        int[] v = toVector(12341);
        generateSubuniverse(v);
        System.out.println(toString(v));
        complement(v);
        generateSubuniverse(v);
        System.out.println(toString(v));
        System.out.println("done");
    }
}
