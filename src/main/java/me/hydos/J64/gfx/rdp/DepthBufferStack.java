package me.hydos.J64.gfx.rdp;

public class DepthBufferStack {
    
    public static class DepthBuffer {
        public boolean cleared;
        public DepthBuffer higher;
        public DepthBuffer lower;
        public int address;
    }

    public DepthBuffer top;
    public DepthBuffer bottom;
    private int numBuffers;
    
    public void init() {
        top = null;
        bottom = null;
        numBuffers = 0;
        System.out.println("Number of buffers: " + numBuffers); // TODO: Actually do something with numBuffers
    }

    public void addTop(DepthBuffer newtop) {
        newtop.lower = top;
        newtop.higher = null;
        
        if (top != null)
            top.higher = newtop;
        
        if (bottom == null)
            bottom = newtop;
        
        top = newtop;
        
        numBuffers++;
    }
    
    public void moveToTop(DepthBuffer newtop) {
        if (newtop == top)
            return;
        
        if (newtop == bottom) {
            bottom = newtop.higher;
            bottom.lower = null;
        } else {
            newtop.higher.lower = newtop.lower;
            newtop.lower.higher = newtop.higher;
        }
        
        newtop.higher = null;
        newtop.lower = top;
        top.higher = newtop;
        top = newtop;
    }
    
    public DepthBuffer findBuffer(int address) {
        DepthBuffer buffer = top;
        
        while (buffer != null) {
            if (buffer.address == address)
                return buffer;
            buffer = buffer.lower;
        }
        
        return null;
    }

}
