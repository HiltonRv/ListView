<?php 
$con = mysqli_connect("localhost","root","","bd_usuario") or die ("Sin Conexion");
$sql = "select * from usuario";
$datos = array();
$resul = mysqli_query($con,$sql);
while($row = mysqli_fetch_object($resul)){
	$datos[] = $row;
}
echo json_encode($datos);
mysqli_close($con);
?>
