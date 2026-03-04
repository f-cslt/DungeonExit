package utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;


public interface MouseAdapter extends MouseMotionListener, MouseListener, MouseWheelListener{

    @Override
    default void mouseClicked(MouseEvent e) {}

    @Override
    default void mouseDragged(MouseEvent e) {}

    @Override
    default void mouseEntered(MouseEvent e) {}

    @Override
    default void mouseExited(MouseEvent e) {}

    @Override
    default void mouseMoved(MouseEvent e) {}

    @Override
    default void mousePressed(MouseEvent e) {}
    
    @Override
    default void mouseReleased(MouseEvent e) {}

    @Override
    default void mouseWheelMoved(MouseWheelEvent e) {}
}
