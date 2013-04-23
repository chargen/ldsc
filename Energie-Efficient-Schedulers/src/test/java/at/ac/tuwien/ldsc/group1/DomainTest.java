package at.ac.tuwien.ldsc.group1;

import org.junit.Assert;
import org.junit.Test;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachineImpl;

public class DomainTest {

    @Test
    public void DomainObjectTest() {
        Application application1 = new ApplicationImpl(200, 5000, 600, 4000,0);
        Application application2 = new ApplicationImpl(100, 3000, 900, 6000,0);

        PhysicalMachine pm1 = new PhysicalMachineImpl(300, 850, 500, 4700, 50000, 2400);
        VirtualMachine vm1 = new VirtualMachineImpl(50, 100, 150, pm1);

        pm1.addComponent(vm1);
        vm1.addComponent(application1);
        vm1.addComponent(application2);
        System.out.println("DomainObjectTest: PhysicalMachine Statistics ");
        System.out.println("RAM: " + pm1.getRam() + " HDD: " + pm1.getHddSize() + " CPU: " + pm1.getCpuInMhz());
        Assert.assertTrue(pm1.getRam() == 650);
        Assert.assertTrue(pm1.getHddSize() == 8950);
        Assert.assertTrue(pm1.getCpuInMhz() == 2150);
    }
    
    @Test
    public void PMVMAvailableResourcesTest() {
    	// int ram, int hddSize, int cpuInMhz, long duration
        Application application1 = new ApplicationImpl(200, 5000, 600, 4000,0);
        Application application2 = new ApplicationImpl(100, 3000, 900, 6000,0);
        
        // int ramBase, int hddBase, int cpuInMhzBase, int ramMax, int hddMax, int cpuInMhzMax
        PhysicalMachine pm1 = new PhysicalMachineImpl(300, 850, 500, 4700, 50000, 2400);
        VirtualMachine vm1 = new VirtualMachineImpl(50, 100, 150, pm1);
        
        // Maybe kill getParent getter
        Assert.assertTrue(vm1.getParent() == pm1);
        
        // Still add it as component because of the costs
        pm1.addComponent(vm1);
        vm1.addComponent(application1);
        vm1.addComponent(application2);
        
        System.out.println("PMVMAvailableResourcesTest Physical Machine Statistics");
        System.out.println("Used RAM: " + pm1.getRam() + " Used  HDD: " + pm1.getHddSize() + " Used CPU: " + pm1.getCpuInMhz());
        Assert.assertTrue(pm1.getRam() == 650);
        Assert.assertTrue(pm1.getHddSize() == 8950);
        Assert.assertTrue(pm1.getCpuInMhz() == 2150);
        
        System.out.println("PMVMAvailableResourcesTest Virtual Machine Statistics");
        System.out.println("Used RAM: " + vm1.getRam() + " Used HDD: " + vm1.getHddSize() + " Used CPU: " + vm1.getCpuInMhz());
        Assert.assertTrue(vm1.getRam() == 350);
        Assert.assertTrue(vm1.getHddSize() == 8100);
        Assert.assertTrue(vm1.getCpuInMhz() == 1650); 
        
        
    }
}
