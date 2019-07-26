<?php
class AccountManager {
    /**
     * @param $user_id
     * @param $user_name
     * @param $email
     * @param $password
     * @return string
     */
    public static function sign_up($user_id, $user_name, $email, $password) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "INSERT INTO user (user_id, user_name, email, password) VALUES (:user_id, :user_name, :email, :password)";
        $id = $db->insert($sql, array(
            'user_id' => $user_id,
            'user_name' => $user_name,
            'email' => $email,
            'password' => password_hash($password, PASSWORD_DEFAULT)
        ));
        return $id;
    }

    /**
     * @param $user_id
     * @param $password
     * @return bool
     */
    public static function sign_in($user_id, $password) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT id, password FROM user WHERE user_id = :user_id OR email = :email";
        $data = $db->fetch($sql, array('user_id' => $user_id, 'email' => $user_id));
        if (is_array($data) && count($data) > 0) {
            if (password_verify($password, $data['password'])) {
                return $data['id'];
            }
        }
        return false;
    }

    /**
     * @param $user_id
     * @return bool
     */
    public static function already_user_id($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT count(*) FROM user WHERE user_id = :user_id";
        $data = $db->fetchColumn($sql, array('user_id' => $user_id));
        return $data == 0 ? true : false;
    }

    /**
     * @param $email
     * @return bool
     */
    public static function already_email($email) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT count(*) FROM user WHERE email = :email";
        $data = $db->fetchColumn($sql, array('email' => $email));
        return $data == 0 ? true : false;
    }
}