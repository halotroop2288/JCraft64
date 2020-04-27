package gln64j.rdp.combiners;

import javax.media.opengl.GL;

public class NVRegisterCombiner implements Combiners.CompiledCombiner {

    public NVRegisterCombiner(Combiners.Combiner c, Combiners.Combiner a) {
        System.err.println("Trying to compile: NV_register_combiners");
    }
    
    public static void init() {
        System.err.println("Trying to initialize: NV_register_combiners");
    }
    
    public void set(GL gl, Combiners combiner) {
        System.err.println("Trying to set combine state: NV_register_combiners");
    }
    
    public void updateColors(GL gl) {
        System.err.println("Trying to update combine colors: NV_register_combiners");
    }

}
