const UserProfile = ({ name, age, gender, image, ...props }) => {
    return (
        <div>
            <h1>{name}</h1>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/${gender}/${image}.jpg`} alt="img"/>
            {props.children}
        </div>
    )
}

export default UserProfile