<?php
require_once '../../libs/functions.php';
require_once '../../libs/TokenManager.php';
require_once '../../libs/ValidationManager.php';
require_once '../../libs/PostManager.php';

if (!isset($_POST['token'], $_POST['content'], $_POST['chara_id'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_POST['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else if (/* !ValidationManager::fire($_POST['content'], ValidationManager::$POST_CONTENT) */ false) {
        $json = ['status' => 'E00', 'msg' => ['POST_CONTENT_LENGTH_OVER']];
    } else {
        if (isset($_FILES['image01'])) {
            $image_flg = 1;
        } else {
            $image_flg = 0;
        }
        $insert_id = PostManager::add_post($user_id, $_POST['content'], $_POST['chara_id'], $image_flg);
        $upload_path = "../../upload/post/";
        $time = time();

        if (isset($_FILES['image01'])) {
            $file_name = $time.'_'.random(10).'.jpg';
            if (move_uploaded_file($_FILES['image01']['tmp_name'], $upload_path.$file_name)) {
                PostManager::add_image($insert_id, 1, $file_name);
            }
        }

        if (isset($_FILES['image02'])) {
            $file_name = $time.'_'.random(10).'.jpg';
            if (move_uploaded_file($_FILES['image02']['tmp_name'], $upload_path.$file_name)) {
                PostManager::add_image($insert_id, 2, $file_name);
            }
        }
        $json = ['status' => 'S00'];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
