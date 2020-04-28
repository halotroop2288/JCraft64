package me.hydos.J64.gln64.rdp.combiners;

public class NVRegisterCombiner implements Combiners.CompiledCombiner {

    public NVRegisterCombiner(Combiners.Combiner c, Combiners.Combiner a) {
        System.err.println("Trying to compile: NV_register_combiners");
    }
    
    public static void init() {
        System.err.println("Trying to initialize: NV_register_combiners");
    }
    
    public void set(Combiners combiner) {
        System.err.println("Trying to set combine state: NV_register_combiners");
    }
    
    public void updateColors() {
        System.err.println("Trying to update combine colors: NV_register_combiners");
    }

}
