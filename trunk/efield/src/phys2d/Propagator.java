// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package phys2d;


// Referenced classes of package phys2d:
//            Particle

public interface Propagator
{

    public abstract void propagate(double d, Particle particle);
}
