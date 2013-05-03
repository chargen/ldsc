package at.ac.tuwien.ldsc.group1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import org.junit.Test;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachineImpl;

public class DomainTest {

    @Test
    public void DomainObjectTest() throws ResourceUnavailableException {
        Application application1 = new ApplicationImpl(
                200,  //RAM
                5000, //HDD
                600,  //CPU
                4000, //Duration
                0     //Timestamp
        );
        Application application2 = new ApplicationImpl(
                100,  //RAM
                3000, //HDD
                900,  //CPU
                6000, //Duration
                0);   //Timestamp

        PhysicalMachine pm1 = new PhysicalMachineImpl(
                300,   //RAM Base
                850,   //HDD Base
                500,   //CPU Base
                4700,  //RAM Max
                50000, //HDD Max
                2400   //CPU Max
        );
        VirtualMachine vm1 = new VirtualMachineImpl(
                50,   //RAM Base
                100,  //HDD Base
                150,  //CPU Base
                pm1);

        vm1.addComponent(application1);
        vm1.addComponent(application2);
        System.out.println("DomainObjectTest: PhysicalMachine Statistics ");
        System.out.println("RAM: " + pm1.getRam() + " HDD: " + pm1.getHddSize() + " CPU: " + pm1.getCpuInMhz());
        assertTrue(pm1.getRam() == 650);
        assertTrue(pm1.getHddSize() == 8950);
        assertTrue(pm1.getCpuInMhz() == 2150);

    }
    
    @Test
    public void PMVMAvailableResourcesTest() throws ResourceUnavailableException {
        Application application1 = new ApplicationImpl(
                200,  //RAM
                5000, //HDD
                600,  //CPU
                4000, //Duration
                0);   //Timestamp
        Application application2 = new ApplicationImpl(
                100,  //RAM
                3000, //HDD
                900,  //CPU
                6000, //Duration
                0);   //Timestamp

        int ramMax = 4700;
        int hddMax = 50000;
        int cpuInMhzMax = 2400;

        PhysicalMachine pm1 = new PhysicalMachineImpl(
                300,   //RAM Base
                850,   //HDD Base
                500,   //CPU Base
                ramMax,  //RAM Max
                hddMax, //HDD Max
                cpuInMhzMax);   //CPU Max
        VirtualMachine vm1 = new VirtualMachineImpl(
                50,   //RAM Base
                100,  //HDD Base
                150,  //CPU Base
                pm1);

        assertTrue(vm1.getParent() == pm1);
        
        // Add the components so that the costs are properly computed
        vm1.addComponent(application1);
        vm1.addComponent(application2);
        
        System.out.println("[PmVmAvailableResourcesTest] Physical Machine Statistics");
        System.out.println("Used RAM: " + pm1.getRam() + ", Used  HDD: " + pm1.getHddSize() + ", Used CPU: " + pm1.getCpuInMhz());
        assertTrue(pm1.getRam() == 650);
        assertTrue(pm1.getHddSize() == 8950);
        assertTrue(pm1.getCpuInMhz() == 2150);
        
        System.out.println("[PmVmAvailableResourcesTest] Virtual Machine Statistics");
        System.out.println("Used RAM: " + vm1.getRam() + ", Used HDD: " + vm1.getHddSize() + ", Used CPU: " + vm1.getCpuInMhz());
        assertEquals(350, vm1.getRam());
        assertEquals(8100, vm1.getHddSize());
        assertEquals(1650, vm1.getCpuInMhz());

        //Assert that: (available VM + used PM) == max on the physical machine
        assertEquals(ramMax, vm1.getRamAvailable() + pm1.getRam());
        assertEquals(hddMax, vm1.getHddAvailable() + pm1.getHddSize());
        assertEquals(cpuInMhzMax, vm1.getCpuAvailable() + pm1.getCpuInMhz());

        //Assert that: (available PM + used PM) == max on the physical machine
        assertEquals(ramMax, pm1.getRamAvailable() + pm1.getRam());
        assertEquals(hddMax, pm1.getHddAvailable() + pm1.getHddSize());
        assertEquals(cpuInMhzMax, pm1.getCpuAvailable() + pm1.getCpuInMhz());
    }
}
