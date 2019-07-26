<?php
require_once '../../libs/TokenManager.php';
require_once '../../libs/ValidationManager.php';
require_once '../../libs/PostManager.php';

//$error_msg = [];

if (!isset($_POST['token'], $_POST['content'], $_POST['anime_id'], $_POST['chara_id'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_POST['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else if (/* !ValidationManager::fire($_POST['content'], ValidationManager::$POST_CONTENT) */ false) {
        $json = ['status' => 'E00', 'msg' => ['POST_CONTENT_LENGTH_OVER']];
    } else {
        PostManager::add_thread($user_id, $_POST['content'], $_POST['anime_id'], $_POST['chara_id']);
        $json = ['status' => 'S00'];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
