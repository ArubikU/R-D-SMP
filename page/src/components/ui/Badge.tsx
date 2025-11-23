import React from 'react';

interface BadgeProps {
    type: string;
}

export const Badge: React.FC<BadgeProps> = ({ type }) => {
    let colors = "bg-gray-700 text-gray-200";
    if (type === "Maldición") colors = "bg-red-900/50 text-red-200 border border-red-700";
    if (type === "Bendición") colors = "bg-green-900/50 text-green-200 border border-green-700";
    if (type === "Caos") colors = "bg-purple-900/50 text-purple-200 border border-purple-700";
    
    return (
        <span className={`px-2 py-0.5 text-xs md:text-sm uppercase tracking-wider rounded ${colors}`}>
            {type}
        </span>
    );
};
