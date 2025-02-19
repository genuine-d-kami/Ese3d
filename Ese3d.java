import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.Arrays;

public class Ese3d extends JComponent{
    private Face[] faces;
    private Vertex[] vertices;

    private float time = 0.0f;

    public static void main(String[] args){
        SwingUtilities.invokeLater(Ese3d::start);
    }

    private static void start(){
        var ese = new Ese3d();
        ese.setPreferredSize(new Dimension(500, 500));

        var frame = new JFrame();
        frame.add(ese);
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }

    private Ese3d(){
        vertices = new Vertex[]{
            new Vertex(-100, -100, -100),
            new Vertex(-100,  100,  100),
            new Vertex(100, -100,  100),
            new Vertex(100,  100, -100)
        };

        faces = new Face[]{
            new Face(0, 3, 2, new Color(0xFF0000)),
            new Face(0, 2, 1, new Color(0x00FF00)),
            new Face(0, 1, 3, new Color(0x0000FF)),
            new Face(1, 2, 3, new Color(0x00FFFF))
        };

        var timer = new Timer(16, e -> {
            Ese3d.updateVertices(vertices, time);
            Ese3d.zsort(faces, vertices);
            repaint();

            time += 0.015f;
        });

        timer.start();
    }

    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.translate(getWidth() / 2, getHeight() / 2);

        for(var face : faces){
            var vertex1 = vertices[face.getIndex(0)];
            var vertex2 = vertices[face.getIndex(1)];
            var vertex3 = vertices[face.getIndex(2)];

            var xlist = new int[]{vertex1.getScreenX(), vertex2.getScreenX(), vertex3.getScreenX()};
            var ylist = new int[]{vertex1.getScreenY(), vertex2.getScreenY(), vertex3.getScreenY()};

            g.setColor(face.getColor());
            g.fillPolygon(xlist, ylist, 3);

            g.setColor(Color.BLACK);
            g.drawPolygon(xlist, ylist, 3);
        }
    }

    private static void updateVertices(Vertex[] vertices, float time){
        float sin = (float)Math.sin(time);
        float cos = (float)Math.cos(time);

        for(var vertex : vertices){
            vertex.rotate(sin, cos);
        }
    }

    private static void zsort(Face[] faces, Vertex[] vertices){
        Arrays.sort(faces, (f1, f2) -> Math.round(f1.calcZ(vertices) - f2.calcZ(vertices)));
    }

    private static class Vertex{
        private float x;
        private float y;
        private float z;

        private float screenX;
        private float screenY;
        private float screenZ;

        private Vertex(float x, float y, float z){
            set(x, y, z);
        }

        private void set(float x, float y, float z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private void rotate(float sin, float cos){
            screenX = x * cos * cos - y * sin + z * cos * sin;
            screenY = x * sin * cos + y * cos + z * sin * sin;
            screenZ = -x * sin + z * cos;
        }

        private float getX(){
            return x;
        }

        private float getY(){
            return y;
        }

        private float getZ(){
            return z;
        }

        private int getScreenX(){
            return Math.round(screenX);
        }

        private int getScreenY(){
            return Math.round(screenY);
        }

        private float getScreenZ(){
            return screenZ;
        }
    }

    private class Face{
        private int[] indices;
        private Color color;

        private Face(int index0, int index1, int index2, Color color){
            indices = new int[3];

            indices[0] = index0;
            indices[1] = index1;
            indices[2] = index2;

            this.color = color;
        }

        private int getIndex(int number){
            return indices[number];
        }

        private Color getColor(){
            return color;
        }

        private float calcZ(Vertex[] vertices){
            return (vertices[indices[0]].getScreenZ() + vertices[indices[1]].getScreenZ() + vertices[indices[2]].getScreenZ()) / 3.0f;
        }
    }
}
