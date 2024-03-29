package com.polydes.dialog.data.transfer;

//http://sakoba.byethost13.com/2011/03/24/java-copy-and-past-image-clipboard/

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ImageTransfer
{
	private static final Logger log = Logger.getLogger(ImageTransfer.class);
	
	public static void copy(Image image)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		ImageSelection selection = new ImageSelection(image);
		clipboard.setContents(selection, null);
	}

	public static BufferedImage paste()
	{
		Image image = null;
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		DataFlavor flavor = DataFlavor.imageFlavor;
		if (clipboard.isDataFlavorAvailable(flavor))
		{
			try
			{
				image = (Image) clipboard.getData(flavor);
			}
			catch (UnsupportedFlavorException e)
			{
				log.error(e.getMessage(), e);
			}
			catch (IOException e)
			{
				log.error(e.getMessage(), e);
			}
		}
		
		if(image == null)
			return null;
		
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.createGraphics();
		g.drawImage(image, 0, 0, w, h, null);
		g.dispose();
		
		return bufferedImage;
	}
	
	public static BufferedImage imgCopy(BufferedImage bi, Rectangle r)
	{
		return imgCopy(bi.getSubimage(r.x, r.y, r.width, r.height));
	}
	
	public static BufferedImage imgCopy(BufferedImage bi, int x, int y, int w, int h)
	{
		return imgCopy(bi.getSubimage(x, y, w, h));
	}
	
	public static BufferedImage imgCopy(BufferedImage bi)
	{
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
}

class ImageSelection implements Transferable
{
	private Image image;
	
	public ImageSelection(Image image)
	{
		this.image = image;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(DataFlavor.imageFlavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if (flavor.equals(DataFlavor.imageFlavor))
		{
			return image;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}
}