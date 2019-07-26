<?php
require_once '../../libs/PostManager.php';

if (!isset($_GET['post_id'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $post_id = PostManager::get_post_id($_GET['post_id']);
    if ($post_id == null) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_POST']];
    } else {
        $data = PostManager::get_image($post_id);
        $json = ['status' => 'S00', 'data' => $data];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
