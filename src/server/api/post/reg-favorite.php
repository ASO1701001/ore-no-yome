<?php
require_once '../../libs/TokenManager.php';
require_once '../../libs/ValidationManager.php';
require_once '../../libs/PostManager.php';

$error_msg = [];

if (!isset($_POST['token'], $_POST['post_id'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_POST['token']);
    $post_id = PostManager::get_post_id($_POST['post_id']);
    if (!$user_id && !$post_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN', 'UNKNOWN_POST']];
    } else if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else if (!$post_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_POST']];
    } else {
        $data = PostManager::reg_favorite($user_id, $post_id);
        $json = ['status' => 'S00', 'data' => $data];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
