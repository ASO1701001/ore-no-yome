<?php
require_once '../../libs/functions.php';
require_once '../../libs/TokenManager.php';
require_once '../../libs/ValidationManager.php';
require_once '../../libs/UserManager.php';
require_once '../../libs/AccountManager.php';

if (!isset($_POST['token'], $_POST['user_name'], $_POST['profile'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_POST['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else {
        $validation_user_name = ValidationManager::fire($_POST['user_name'], ValidationManager::$USER_NAME);
        $validation_profile = ValidationManager::fire($_POST['profile'], ValidationManager::$PROFILE);
        if (!$validation_user_name) {
            if (!$validation_user_name) $error_msg[] = "VALIDATION_USER_NAME";
            if (!$validation_profile) $error_msg[] = "VALIDATION_PROFILE";
            $json = ['status' => 'E00', 'msg' => $error_msg];
        } else {
            $time = time();
            $upload_path = "../../upload/icon/";
            $file_name = null;
            if (isset($_FILES['image01'])) {
                $file_name = $time.'_'.random(10).'.jpg';
                if (move_uploaded_file($_FILES['image01']['tmp_name'], $upload_path.$file_name)) {
                    UserManager::edit_my_info(
                        $user_id,
                        $_POST['user_name'],
                        $_POST['profile'],
                        $file_name
                    );
                    $json = ['status' => 'S00'];
                } else {
                    $json = ['status' => 'E00', 'msg' => ['FILE_UPLOAD_FAIL']];
                }
            } else {
                UserManager::edit_my_info(
                    $user_id,
                    $_POST['user_name'],
                    $_POST['profile'],
                    null
                );
                $json = ['status' => 'S00'];
            }
        }
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
