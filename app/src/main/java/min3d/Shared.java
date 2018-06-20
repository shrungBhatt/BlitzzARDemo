package min3d;

import min3d.core.TextureManager;
import android.content.Context;

import com.projects.shrungbhatt.blitzzardemo.GLRenderer;

/**
 * Holds static references to TextureManager, Renderer, and the application Context. 
 */
public class Shared 
{
	private static Context _context;
	private static GLRenderer _renderer;
	private static TextureManager _textureManager;

	
	public static Context context()
	{
		return _context;
	}
	public static void context(Context $c)
	{
		_context = $c;
	}

	public static GLRenderer renderer()
	{
		return _renderer;
	}
	public static void renderer(GLRenderer $r)
	{
		_renderer = $r;
	}
	
	/**
	 * You must access the TextureManager instance through this accessor
	 */
	public static TextureManager textureManager()
	{
		return _textureManager;
	}
	public static void textureManager(TextureManager $bm)
	{
		_textureManager = $bm;
	}
}
