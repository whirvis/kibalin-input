package io.ketill.glfw.psx;

import io.ketill.IoFeature;
import io.ketill.RegisteredFeature;
import io.ketill.psx.Ps4Controller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

class GlfwPs4AdapterTest {

    private static final Random RANDOM = new Random();

    private long ptr_glfwWindow;
    private int glfwJoystick;
    private Ps4Controller controller;

    @BeforeEach
    void setup() {
        assumeTrue(glfwInit());

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        this.ptr_glfwWindow = glfwCreateWindow(1024, 768, "", 0L, 0L);
        this.glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);

        this.controller = GlfwPs4Adapter.wrangle(ptr_glfwWindow, glfwJoystick);
    }

    @Test
    void ensureIntendedFeaturesSupported() {
        List<IoFeature<?>> unsupported = new ArrayList<>();
        unsupported.add(Ps4Controller.MOTOR_STRONG);
        unsupported.add(Ps4Controller.MOTOR_WEAK);
        unsupported.add(Ps4Controller.FEATURE_LIGHTBAR);

        for (RegisteredFeature<?, ?> rf : controller.getFeatures()) {
            if (!unsupported.contains(rf.feature)) {
                assertTrue(controller.isFeatureSupported(rf.feature));
            }
        }
    }

    @Test
    void updateStick() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            /* generate axis values for next test */
            float lsYValue = RANDOM.nextFloat();
            float rsYValue = RANDOM.nextFloat();
            axes.put(GlfwPs4Adapter.MAPPING_LS.glfwYAxis, lsYValue);
            axes.put(GlfwPs4Adapter.MAPPING_RS.glfwYAxis, rsYValue);

            controller.poll(); /* update sticks */

            lsYValue *= -1.0F;
            rsYValue *= -1.0F;
            assertEquals(lsYValue, controller.ls.y());
            assertEquals(rsYValue, controller.rs.y());
        }
    }

    @Test
    void updateTrigger() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            FloatBuffer axes = FloatBuffer.allocate(16);
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            /* generate axis values for next test */
            float ltValue = RANDOM.nextFloat();
            float rtValue = RANDOM.nextFloat();
            axes.put(GlfwPs4Adapter.AXIS_LT, ltValue);
            axes.put(GlfwPs4Adapter.AXIS_RT, rtValue);

            controller.poll(); /* update triggers */

            ltValue = (ltValue + 1.0F) / 2.0F;
            rtValue = (rtValue + 1.0F) / 2.0F;
            assertEquals(ltValue, controller.lt.force());
            assertEquals(rtValue, controller.rt.force());
        }
    }

    @AfterEach
    void shutdown() {
        if (ptr_glfwWindow != 0x00) {
            glfwDestroyWindow(ptr_glfwWindow);
            glfwTerminate();
        }
    }

}