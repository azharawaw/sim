/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.input;

import java.util.HashMap;

import javax.swing.ImageIcon;

import jmetest.terrain.TestTerrain;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.ChaseCamera;
import com.jme.input.ThirdPersonHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainPage;
import com.jmex.terrain.util.FaultFractalHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

public class ControlImplementor extends SimpleCanvasImpl {

    public Node m_character;

    public ChaseCamera chaser;

    public TerrainPage page;

    public ThirdPersonHandler input;

    public long startTime;

    public Geometry target;

    public ControlImplementor(int width, int height) {
        super(width, height);
    }

    public void simpleSetup() {
        setupCharacter();
        setupTerrain();
        setupChaseCamera();
        setupInput();
        startTime = System.currentTimeMillis() + 5000;
    }
    
    public void simpleUpdate() {
        input.update(tpf);
        chaser.update(tpf);

        if (!Vector3f.isValidVector(cam.getLocation()))
            cam.getLocation().set(0,0,0);

        float camMinHeight = page.getHeight(cam.getLocation()) + 2f;
        if (!Float.isInfinite(camMinHeight) && !Float.isNaN(camMinHeight)
                && cam.getLocation().y <= camMinHeight) {
            cam.getLocation().y = camMinHeight;
            cam.update();
        }

        float characterMinHeight = page.getHeight(m_character
                .getLocalTranslation())+((BoundingBox)m_character.getWorldBound()).yExtent;
        if (!Float.isInfinite(characterMinHeight) && !Float.isNaN(characterMinHeight)) {
            m_character.getLocalTranslation().y = characterMinHeight;
        }

//        page.getSurfaceNormal(m_character.getLocalTranslation(), normal);
//        if (normal != null)
//            m_character.rotateUpTo(normal);
    }

    private void setupCharacter() {
        target = new Box("box", new Vector3f(), .5f,.5f,.5f);
        target.setLocalScale(10);
        target.setModelBound(new BoundingBox());
        target.updateModelBound();
        m_character = new Node("char node");
        rootNode.attachChild(m_character);
        m_character.attachChild(target);
        m_character.getLocalTranslation().y=255;
        m_character.updateWorldBound(); // We do this to allow the camera setup access to the world bound in our setup code.

        TextureState ts = renderer.createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestThirdPersonController.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MinificationFilter.BilinearNearestMipMap,
            Texture.MagnificationFilter.Bilinear));
        m_character.setRenderState(ts);
    }
    
    private void setupTerrain() {
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);

        renderer.setBackgroundColor(
                new ColorRGBA(0.5f, 0.5f, 0.5f, 1));

        DirectionalLight dr = new DirectionalLight();
        dr.setEnabled(true);
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0.5f, -0.5f, 0));

        CullState cs = renderer.createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        LightState lightState = renderer.createLightState();
        lightState.setEnabled(true);
        lightState.attach(dr);
        rootNode.setRenderState(lightState);

        FaultFractalHeightMap heightMap = new FaultFractalHeightMap(257, 32, 0,
                255, 0.75f);
        Vector3f terrainScale = new Vector3f(10, 1, 10);
        heightMap.setHeightScale(0.001f);
        page = new TerrainPage("Terrain", 33, heightMap.getSize(),
                terrainScale, heightMap.getHeightMap());

        page.setDetailTexture(1, 16);
        rootNode.attachChild(page);

        ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
                heightMap);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/grassb.png")), -128, 0, 128);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/dirt.jpg")), 0, 128, 255);
        pt.addTexture(new ImageIcon(TestTerrain.class.getClassLoader()
                .getResource("jmetest/data/texture/highest.jpg")), 128, 255,
                384);

        pt.createTexture(512);

        TextureState ts = renderer.createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(pt.getImageIcon().getImage(),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear, true);
        ts.setTexture(t1, 0);

        Texture t2 = TextureManager.loadTexture(TestThirdPersonController.class
                .getClassLoader()
                .getResource("jmetest/data/texture/Detail.jpg"),
                Texture.MinificationFilter.Trilinear, Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t2, 1);
        t2.setWrap(Texture.WrapMode.Repeat);

        t1.setApply(Texture.ApplyMode.Combine);
        t1.setCombineFuncRGB(Texture.CombinerFunctionRGB.Modulate);
        t1.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t1.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t1.setCombineSrc1RGB(Texture.CombinerSource.PrimaryColor);
        t1.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);

        t2.setApply(Texture.ApplyMode.Combine);
        t2.setCombineFuncRGB(Texture.CombinerFunctionRGB.AddSigned);
        t2.setCombineSrc0RGB(Texture.CombinerSource.CurrentTexture);
        t2.setCombineOp0RGB(Texture.CombinerOperandRGB.SourceColor);
        t2.setCombineSrc1RGB(Texture.CombinerSource.Previous);
        t2.setCombineOp1RGB(Texture.CombinerOperandRGB.SourceColor);
        rootNode.setRenderState(ts);

        FogState fs = renderer.createFogState();
        fs.setDensity(0.5f);
        fs.setEnabled(true);
        fs.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        fs.setEnd(1000);
        fs.setStart(500);
        fs.setDensityFunction(FogState.DensityFunction.Linear);
        fs.setQuality(FogState.Quality.PerVertex);
        rootNode.setRenderState(fs);
    }

    private void setupChaseCamera() {
        Vector3f targetOffset = new Vector3f();
        targetOffset.y = ((BoundingBox) m_character.getWorldBound()).yExtent * 1.5f;
        
        float camMinHeight = page.getHeight(cam.getLocation()) + 2f;
        if (!Float.isInfinite(camMinHeight) && !Float.isNaN(camMinHeight)
                && cam.getLocation().y <= camMinHeight) {
            cam.getLocation().y = camMinHeight;
            cam.update();
        }

        chaser = new ChaseCamera(cam, m_character);
        chaser.setTargetOffset(targetOffset);
    }

    private void setupInput() {
        HashMap<String, Object> handlerProps = new HashMap<String, Object>();
        handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
        handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, ""+(1.0f * FastMath.PI));
        handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "false");
        handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "true");
        input = new ThirdPersonHandler(m_character, cam, handlerProps);
        input.setActionSpeed(100f);
    }
}
