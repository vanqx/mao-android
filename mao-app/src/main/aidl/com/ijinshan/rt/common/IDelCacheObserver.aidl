package com.ijinshan.rt.common;

oneway interface IDelCacheObserver {
	void onRemoveCompleted(in List<String> pathList);
}