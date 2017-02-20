package util;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件管理类<br>
 * 主要完成文件大小的读取，分类以及删除
 * @author Administrator
 *
 */

public class FileManager {
	/** 内置存储卡目录(可能为null) */
	public static File inSdcardDir = null;
	/** 外置存储卡目录(可能为null) */
	public static File outSdcardDir = null;

	static {
		// 如果有手机内置sdcard卡路径,进行内置Fiel实例化(防止File file = new File(null))
		if (MemoryManager.getPhoneInSDCardPath() != null) {
			inSdcardDir = null;
			inSdcardDir = new File(MemoryManager.getPhoneInSDCardPath());
		}
		// 如果有手机外置sdcard卡路径,进行外置Fiel实例化(防止File file = new File(null))
		if (MemoryManager.getPhoneOutSDCardPath() != null) {
			outSdcardDir = null;
			outSdcardDir = new File(MemoryManager.getPhoneOutSDCardPath());
		}
	}

	// 单例(单态)--------------------------------------------------------------------------------------
	private FileManager() {
	}

	private static FileManager fileManager = new FileManager();
	/**单例模式*/
	public static FileManager getFileManager() {
		return fileManager;
	}

	// 中止搜索的控制------------------------------------------------------------------------------------

	/** 是否停止搜索 */
	private boolean isStopSearch = false;
	/**
	 * {@link #isStopSearch}
	 * @return
	 */
	public boolean isStopSearch() {
		return isStopSearch;
	}
	/**
	 * {@link #isStopSearch}
	 * @return
	 */
	public void setStopSearch(boolean isStopSearch) {
		this.isStopSearch = isStopSearch;
	}

	// 文件搜索-----------------------------------------------------------------------
	/** 任意文件(非目录)集合 */
	private ArrayList<FileInfo> anyFileList = new ArrayList<FileInfo>();
	private long anyFileSize; // 任意文件总大小(在搜索过程中，实时迭加，计算总大小)
	/** 文档文件集合 */
	private ArrayList<FileInfo> txtFileList = new ArrayList<FileInfo>();
	private long txtFileSize; // 文档文件总大小(同上)
	/** 视频文件集合 */
	private ArrayList<FileInfo> videoFileList = new ArrayList<FileInfo>();
	private long videoFileSize; // 视频文件总大小(同上)
	/** 音乐文件集合 */
	private ArrayList<FileInfo> audioFileList = new ArrayList<FileInfo>();
	private long audioFileSize; // 音乐文件总大小(同上)
	/** 图像文件集合 */
	private ArrayList<FileInfo> imageFileList = new ArrayList<FileInfo>();
	private long imageFileSize; // 图像文件总大小(同上)
	/** ZIP文件集合 */
	private ArrayList<FileInfo> zipFileList = new ArrayList<FileInfo>();
	private long zipFileSize; // ZIP文件总大小(同上)
	/** APK文件集合 */
	private ArrayList<FileInfo> apkFileList = new ArrayList<FileInfo>();
	private long apkFileSize; // APK文件总大小(同上)

	// 初始化相关变量(在每次重新开始搜索前,如searchSDCardFile())------------------------------------------------
	private void initData() {
		isStopSearch = false;
		anyFileSize = 0;
		txtFileSize = 0;
		videoFileSize = 0;
		audioFileSize = 0;
		imageFileSize = 0;
		zipFileSize = 0;
		apkFileSize = 0;
		anyFileList.clear();
		txtFileList.clear();
		videoFileList.clear();
		audioFileList.clear();
		imageFileList.clear();
		zipFileList.clear();
		apkFileList.clear();
	}

	/** 搜索存储卡目录下的所有文件,结果实时保存在 {@link #anyFileList}内 */
	public void searchSDCardFile() {
		if (anyFileList == null || anyFileList.size() <= 0) {
			initData();
			searchFile(inSdcardDir, false); // 传入false标记, 不让运算反溃结束
			searchFile(outSdcardDir, true); // 传入true标记, 让运算反溃结束
		} else {
			// 直接回调非异常结束
			callbackSearchFileListenerEnd(false);
		}
	}

	/** (未用到)搜索指定目录下的所有文件,结果实时保存在 {@link #anyFileList}内 */
	public void searchFile(File file) {
		initData();
		searchFile(file, true);
	}

	/** (未用到)搜索内置存储卡目录下的所有文件,结果实时保存在 {@link #anyFileList}内 */
	public void searchINSDCardFile() {
		initData();
		searchFile(inSdcardDir, true);
	}

	/** (未用到)搜索外置存储卡目录下的所有文件,结果实时保存在 {@link #anyFileList}内 */
	public void searchOUTSDCardFile() {
		initData();
		searchFile(outSdcardDir, true);
	}

	/**
	 * 递归搜索所有文件
	 * 
	 * @param file
	 * @param flag
	 *            为递归结束判断准备,每次调用方法栈内的flag值都不一样,第一次调用的结束才是真正结束(才会调用反馈结束方法)
	 */
	private void searchFile(File file, boolean isFirstRunFlag) {
		// ----中止搜索------
		if (isStopSearch) {
			// 是首次运行的结束(搜索结束)
			if (isFirstRunFlag) {
				callbackSearchFileListenerEnd(true);// 回调接口end()方法,搜索结束(异常结束)
			}
			return;
		}
		// #1 排除"不正常"文件
		if (file == null || !file.canRead() || !file.exists()) {
			// 是首次运行的结束(搜索结束)
			if (isFirstRunFlag) {
				callbackSearchFileListenerEnd(true);// 回调接口end()方法,搜索结束(异常结束)
			}
			return;
		}
		// #2 如果是文件(非目录)
		if (!file.isDirectory()) {
			// 判断文件大小
			if (file.length() <= 0) {
				return;
			}
			//如果文件名称中没有“.”  未知文件类型
			if (file.getName().lastIndexOf('.') == -1) {
				return;
			}
			//获取图标以及文件类型
			String[] iconAndTypeNames = FileTypeUtil
					.getFileIconAndTypeName(file);
			final String iconName = iconAndTypeNames[0]; // 此文件使用什么图像(在Res中的图标文件名称)
			final String typeName = iconAndTypeNames[1]; // 此文件是属什么类型的
			// 添加到所有文件的集合
			FileInfo fileInfo = new FileInfo(file, iconName, typeName);
			anyFileList.add(fileInfo);
			// 迭加计算总文件大小
			anyFileSize += file.length();
			// 分类
			if (typeName.equals(FileTypeUtil.TYPE_APK)) {
				apkFileSize += file.length();
				apkFileList.add(fileInfo);
			} else if (typeName.equals(FileTypeUtil.TYPE_AUDIO)) {
				audioFileSize += file.length();
				audioFileList.add(fileInfo);
			} else if (typeName.equals(FileTypeUtil.TYPE_IMAGE)) {
				imageFileSize += file.length();
				imageFileList.add(fileInfo);
			} else if (typeName.equals(FileTypeUtil.TYPE_TXT)) {
				txtFileSize += file.length();
				txtFileList.add(fileInfo);
			} else if (typeName.equals(FileTypeUtil.TYPE_VIDEO)) {
				videoFileSize += file.length();
				videoFileList.add(fileInfo);
			} else if (typeName.equals(FileTypeUtil.TYPE_ZIP)) {
				zipFileSize += file.length();
				zipFileList.add(fileInfo);
			}
			// 回调接口searching方法(用作通知调用者数据更新了)
			callbackSearchFileListenerSearching(typeName);
			return;
		}
		// #3 如果是目录
		File[] files = file.listFiles();
		if (files == null || files.length <= 0) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File tmpFile = files[i];
			searchFile(tmpFile, false);// 递归,以后的方法栈内的flag值都为false,表示不再是第一次的调用
		}
		// 是首次运行的结束(搜索结束)
		if (isFirstRunFlag) {
			callbackSearchFileListenerEnd(false);// 回调接口end()方法,搜索结束，完成，非异常结束
		}
	}

	/** 获取所有文件文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getAnyFileList() {
		return anyFileList;
	}

	/** 获取文档文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getTxtFileList() {
		return txtFileList;
	}

	/** 获取视频文件文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getVideoFileList() {
		return videoFileList;
	}

	/** 获取音乐文件文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getAudioFileList() {
		return audioFileList;
	}

	/** 获取图像文件文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getImageFileList() {
		return imageFileList;
	}

	/** 获取APK文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getApkFileList() {
		return apkFileList;
	}

	/** 获取zip文件列表(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public ArrayList<FileInfo> getZipFileList() {
		return zipFileList;
	}

	/** 获取APK文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getApkFileSize() {
		return apkFileSize;
	}

	/** 获取zip文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getZipFileSize() {
		return zipFileSize;
	}

	/** 获取任意所有文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getAnyFileSize() {
		return anyFileSize;
	}

	/** 获取文本文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getTxtFileSize() {
		return txtFileSize;
	}

	/** 获取视频文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getVideoFileSize() {
		return videoFileSize;
	}

	/** 获取音频文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getAudioFileSize() {
		return audioFileSize;
	}

	/** 获取图像文件当前总大小(搜索过程中{@link #searchSDCardFile()}实时在变化) */
	public long getImageFileSize() {
		return imageFileSize;
	}

	public void setAnyFileSize(long anyFileSize) {
		this.anyFileSize = anyFileSize;
		if (this.anyFileSize < 0) {
			this.anyFileSize = 0;
		}
	}

	public void setTxtFileSize(long txtFileSize) {
		this.txtFileSize = txtFileSize;
		if (this.txtFileSize < 0) {
			this.txtFileSize = 0;
		}
	}

	public void setVideoFileSize(long videoFileSize) {
		this.videoFileSize = videoFileSize;
		if (this.videoFileSize < 0) {
			this.videoFileSize = 0;
		}
	}

	public void setAudioFileSize(long audioFileSize) {
		this.audioFileSize = audioFileSize;
		if (this.audioFileSize < 0) {
			this.audioFileSize = 0;
		}
	}

	public void setImageFileSize(long imageFileSize) {
		this.imageFileSize = imageFileSize;
		if (this.imageFileSize < 0) {
			this.imageFileSize = 0;
		}
	}

	public void setZipFileSize(long zipFileSize) {
		this.zipFileSize = zipFileSize;
		if (this.zipFileSize < 0) {
			this.zipFileSize = 0;
		}
	}

	public void setApkFileSize(long apkFileSize) {
		this.apkFileSize = apkFileSize;
		if (this.apkFileSize < 0) {
			this.apkFileSize = 0;
		}
	}

	// 搜索过程监听-----------------------------------------------------------------------------------
	private SearchFileListener listener; // 搜索过程监听

	/**
	 * 搜索过程的实时监听{@link #setSearchFileListener(SearchFileListener listener)},
	 * 用做搜索过程实时反溃文件信息
	 */
	public interface SearchFileListener {
		/**
		 * 当搜索过程中，每搜索到一个文件将调用
		 * 
		 * @see 想要获取当前数据集合及数据量，可用getApkFileSize()和 getApkFileList()实时获取
		 */
		void searching(String typeName);

		/** 当搜索结束时将调用 */
		void end(boolean isExceptionEnd);
	}
	/** 设置文件查找监听*/
	public void setSearchFileListener(SearchFileListener listener) {
		this.listener = listener;
	}

	/** 用来回调 SearchFileListener接口内方法 */
	private void callbackSearchFileListenerSearching(String typeName) {
		if (listener != null) {
			listener.searching(typeName);
		}
	}

	/** 用来回调 SearchFileListener接口内方法 */
	private void callbackSearchFileListenerEnd(boolean isExceptionEnd) {
		if (listener != null) {
			listener.end(isExceptionEnd);
		}
	}

	/** 取得文件或文件夹大小 */
	public static long getFileSize(File file) {
		long size = 0;
		if (!file.isDirectory()) { // 文件
			return file.length();
		}
		File files[] = file.listFiles(); // 文件夹（递归）
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				size = size + getFileSize(files[i]);
			} else {
				size = size + files[i].length();
			}
		}
		return size;
	}

	/** 删除文件 **/
	public void deleteFile(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; ++i) {
					deleteFile(files[i]);
				}
			}
		}
		f.delete();
	}
}