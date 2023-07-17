import UserProfile from "./UserProfile.jsx";

const UserProfiles = ({users}) => {
    return (
        <div>
            {users.map((user, index) => (
                <UserProfile
                    key={index}
                    name={user.name}
                    age={user.age}
                    gender={user.gender === 'MALE' ? 'men' : 'women'}
                    image={index}
                />
            ))}
        </div>
    )
}

export default UserProfiles